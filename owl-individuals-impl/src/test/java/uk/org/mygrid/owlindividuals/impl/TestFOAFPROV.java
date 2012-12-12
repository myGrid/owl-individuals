package uk.org.mygrid.owlindividuals.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.org.mygrid.owlindividuals.api.Individuals;
import uk.org.mygrid.owlindividuals.api.UnknownClassException;
import uk.org.mygrid.owlindividuals.api.UnknownObjectPropertyException;

public class TestFOAFPROV {

	private static Path testFile;

	@Test
	public void individualsEmptyContructor() throws Exception {
		Individuals individuals = new IndividualsImpl();
		assertNotNull(individuals.getManager());
		assertNotNull(individuals.getOntology());
	}
	
	@Test
	public void individualsDetailedContructor() throws Exception {	
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.createOntology();
		Individuals individuals = new IndividualsImpl(manager, ontology);
		assertSame(manager, individuals.getManager());
		assertSame(ontology, individuals.getOntology());

	}

	@Test
	public void loadFileManually() throws Exception {
		if (testFile == null) {
			// Run save first
			saveManually();
		}

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		// Create ontology with two imports
		IRI provIri = IRI.create("http://www.w3.org/ns/prov");
		@SuppressWarnings("unused")
		OWLOntology prov = manager.loadOntology(provIri);
		IRI foafIri = IRI.create("http://xmlns.com/foaf/0.1");
		@SuppressWarnings("unused")
		OWLOntology foaf = manager.loadOntology(foafIri);

		OWLDataFactory factory = manager.getOWLDataFactory();

		DefaultPrefixManager pm = new DefaultPrefixManager();
		pm.setPrefix("foaf:", "http://xmlns.com/foaf/0.1/");
		pm.setPrefix("prov:", "http://www.w3.org/ns/prov#");

		OWLClass entityClass = factory.getOWLClass("prov:Entity", pm);

		OWLOntology ontology;

		String ttl = new String(Files.readAllBytes(testFile), Charset.forName("utf-8"));
		// Workaround for foaf:null bug
		ttl = ttl.replaceAll(":null", ":");		
		ontology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ttl));

		for (OWLIndividual entity : entityClass.getIndividuals(ontology)) {
			System.out.println("Entity " + entity);
			assertEquals("index.html", entity.asOWLNamedIndividual().getIRI()
					.toString());
			OWLObjectProperty wasAttributedTo = factory.getOWLObjectProperty(
					"prov:wasAttributedTo", pm);
			Set<OWLIndividual> attributed = entity.getObjectPropertyValues(
					wasAttributedTo, ontology);
			OWLIndividual me = attributed.iterator().next();

			System.out.println("  prov:wasAttributedTo " + me);
			assertEquals("http://soiland-reyes.com/stian/#me", me
					.asOWLNamedIndividual().getIRI().toString());

			OWLClass personClass = factory.getOWLClass("foaf:Person", pm);
			assertTrue(me.getTypes(ontology).contains(personClass));

			OWLDataProperty name = factory.getOWLDataProperty("foaf:lastName",
					pm);
			assertEquals("Soiland-Reyes",
					me.getDataPropertyValues(name, ontology).iterator().next()
							.getLiteral());
		}
	}

	@Test
	public void loadFileUsingIndividuals() throws Exception {
		// Run save first
		saveUsingIndividuals();

		Individuals individuals = new IndividualsImpl();
		individuals.setStrict(true);
		individuals.importOntology("prov", "http://www.w3.org/ns/prov#");
		individuals.importOntology("foaf", "http://xmlns.com/foaf/0.1/");
		
		String ttl = new String(Files.readAllBytes(testFile), Charset.forName("utf-8"));
		// Workaround for foaf:null bug
		ttl = ttl.replaceAll(":null", ":");		
		individuals.loadOntologyFromString(ttl);
		
		for (OWLIndividual entity : individuals.getIndividualsOfType("prov:Entity")) {
			assertEquals("index.html", individuals.toIRI(entity));
			String predicateCurie = "prov:wasAttributedTo";
			Set<OWLIndividual> attributed = individuals.getObjectProperties(entity,
					predicateCurie);
			assertEquals(1, attributed.size());
			OWLIndividual me = individuals
					.getObjectProperty(entity, predicateCurie);
			System.out.println("  prov:wasAttributedTo " + me);
			assertEquals("http://soiland-reyes.com/stian/#me",
					individuals.toIRI(me));
			assertTrue(individuals.isInstanceOf(me, "foaf:Person"));
			assertEquals("Soiland-Reyes",
					individuals.getDataProperty(me, "foaf:lastName"));
		}
	}

	@Test
	public void saveManually() throws Exception {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();

		// Create ontology with two imports
		IRI ontologyIRI = IRI.create("prov.ttl");
		OWLOntology ontology = manager.createOntology(ontologyIRI);
		IRI provIri = IRI.create("http://www.w3.org/ns/prov");
		@SuppressWarnings("unused")
		OWLOntology prov = manager.loadOntology(provIri);
		IRI foafIri = IRI.create("http://xmlns.com/foaf/0.1");
		@SuppressWarnings("unused")
		OWLOntology foaf = manager.loadOntology(foafIri);

		DefaultPrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix(ontologyIRI + "#");
		pm.setPrefix("foaf:", "http://xmlns.com/foaf/0.1/");
		pm.setPrefix("prov:", "http://www.w3.org/ns/prov#");

		// NOTE: We can't use the correct URIs (as above) here, as it will
		// wrongly be output as
		// "prov:null" in the Turtle output
		OWLImportsDeclaration provImport = factory
				.getOWLImportsDeclaration(provIri);
		manager.applyChange(new AddImport(ontology, provImport));
		OWLImportsDeclaration foafImport = factory
				.getOWLImportsDeclaration(foafIri);
		manager.applyChange(new AddImport(ontology, foafImport));

		OWLNamedIndividual index = factory.getOWLNamedIndividual(IRI
				.create("index.html"));
		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(index));

		OWLClass entityClass = factory.getOWLClass("prov:Entity", pm);
		manager.addAxiom(ontology,
				factory.getOWLClassAssertionAxiom(entityClass, index));

		OWLObjectProperty wasAttributedTo = factory.getOWLObjectProperty(
				"prov:wasAttributedTo", pm);
		OWLIndividual me = factory.getOWLNamedIndividual(IRI
				.create("http://soiland-reyes.com/stian/#me"));
		// The order of these parameters is a bit odd.. predicate/subject/object
		manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(
				wasAttributedTo, index, me));

		OWLClass personClass = factory.getOWLClass("foaf:Person", pm);
		manager.addAxiom(ontology,
				factory.getOWLClassAssertionAxiom(personClass, me));

		// Note: Using foaf:lastName instead of foaf:name
		// because it is an invalid dataproperty subproperty of annotation
		// property:
		//
		// http://protege-ontology-editor-knowledge-acquisition-system.136.n4.nabble.com/Custom-import-ontology-button-problem-td3684903.html

		OWLDataProperty name = factory.getOWLDataProperty("foaf:lastName", pm);
		OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory
				.getOWLDataPropertyAssertionAxiom(name, me, "Soiland-Reyes");
		manager.addAxiom(ontology, dataPropertyAssertion);

		RDFXMLOntologyFormat rdfFormat = new RDFXMLOntologyFormat();
		rdfFormat.copyPrefixesFrom(pm);
		rdfFormat.setDefaultPrefix(ontologyIRI + "#");

		TurtleOntologyFormat turtleFormat = new TurtleOntologyFormat();
		turtleFormat.copyPrefixesFrom(pm);
		turtleFormat.setDefaultPrefix(ontologyIRI + "#");

		testFile = Files.createTempFile("prov", ".ttl");
		try (OutputStream outputStream = Files.newOutputStream(testFile)) {
			manager.saveOntology(ontology, turtleFormat, outputStream);
		}
		System.out.println(testFile);
	}

	@Test
	public void saveUsingIndividuals() throws Exception {
		Individuals individuals = new IndividualsImpl();
		individuals.setStrict(true);
		individuals.importOntology("prov", "http://www.w3.org/ns/prov#");
		individuals.importOntology("foaf", "http://xmlns.com/foaf/0.1/");

		OWLNamedIndividual index = individuals.createIndividual("<index.html>"
//				,"prov:Entity"
//				,"foaf:Document"
				);
		OWLNamedIndividual me = individuals.createIndividual(
				"<http://soiland-reyes.com/stian/#me>" 
//				,"prov:Agent"
//				,"foaf:Person"
				);
		
		individuals.addObjectProperty(index, "prov:wasAttributedTo", me);
		individuals.addDataPropertyPlain(me, "foaf:givenName", "Soiland-Reyes");
//		individuals.fillInOntology();
//		individuals.fillInOntology(new Reasoner.ReasonerFactory().createReasoner(individuals.getOntology()));
//		individuals.fillInOntology(new PelletReasonerFactory().createReasoner(individuals.getOntology()));
//		individuals.fillInOntology(new JFactFactory().createReasoner(individuals.getOntology()));
		// Disabled as the ELK reasoner can't do object properties and this fails with
		// UnsupportedOperationException
//		individuals.fillInOntology(new ElkReasonerFactory().createReasoner(individuals.getOntology()));
		
		

		testFile = Files.createTempFile("prov", ".ttl");
		try (OutputStream outputStream = Files.newOutputStream(testFile)) {
			individuals.saveOntology(outputStream);
		}
		System.out.println(testFile);
		System.out.write(Files.readAllBytes(testFile));

		try {
			individuals.createIndividual("<text>", "prov:Enntity");
			fail("Did not fail on unknown class");
		} catch (UnknownClassException ex) {
			// my N key is stuck!
		}

		try {
			individuals.addObjectProperty(index, "foaf:wasAttributedTo", me);
			fail("Did not fail on unknown property");
		} catch (UnknownObjectPropertyException ex) {
			// Args, should have been prov: prefix
		}
	}

}
