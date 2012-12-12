package uk.org.mygrid.owlindividuals.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import uk.org.mygrid.owlindividuals.api.AlreadyExistsException;
import uk.org.mygrid.owlindividuals.api.Individuals;
import uk.org.mygrid.owlindividuals.api.MultipleFoundException;
import uk.org.mygrid.owlindividuals.api.UnknownAnnotationPropertyException;
import uk.org.mygrid.owlindividuals.api.UnknownClassException;
import uk.org.mygrid.owlindividuals.api.UnknownDataPropertyException;
import uk.org.mygrid.owlindividuals.api.UnknownDataTypeException;
import uk.org.mygrid.owlindividuals.api.UnknownIndividualException;
import uk.org.mygrid.owlindividuals.api.UnknownObjectPropertyException;

public class IndividualsImpl implements Individuals {

	protected static <T> T firstOfSet(Set<T> set, String errorMsg) {
		if (set.isEmpty()) {
			return null;
		}
		if (set.size() > 1) {
			throw new MultipleFoundException(errorMsg);
		}
		return set.iterator().next();
	}
	private OWLDataFactory factory;
	private boolean isStrictClasses;
	private boolean isStrictDatatypes;
	private boolean isStrictIndividuals;
	private boolean isStrictProperties;
	private OWLOntologyManager manager;
	private OWLOntology ontology;

	private DefaultPrefixManager prefixManager = new DefaultPrefixManager();

	public IndividualsImpl() throws OWLOntologyCreationException {
		setManager(OWLManager.createOWLOntologyManager());
		setOntology(getManager().createOntology());
	}

	public IndividualsImpl(OWLOntologyManager manager, OWLOntology ontology) {
		this.setManager(manager);
		this.setOntology(ontology);
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addAnnotationProperty(org.semanticweb.owlapi.model.OWLAnnotationSubject, java.lang.String, org.semanticweb.owlapi.model.OWLAnnotationValue)
	 */
	@Override
	public void addAnnotationProperty(OWLAnnotationSubject subject,
			String predicateCurie, OWLAnnotationValue value) {
		OWLAnnotationProperty annProp = getOWLAnnotationProperty(predicateCurie);
		manager.addAxiom(ontology,
				factory.getOWLAnnotationAssertionAxiom(annProp, subject, value));
	}
	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addAnnotationProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, boolean)
	 */
	@Override
	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, boolean value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addAnnotationProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, double)
	 */
	@Override
	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, double value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addAnnotationProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, float)
	 */
	@Override
	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, float value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addAnnotationProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, int)
	 */
	@Override
	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, int value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addAnnotationProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, org.semanticweb.owlapi.model.OWLIndividual)
	 */
	@Override
	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, OWLIndividual object) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				toAnnotationValue(object));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addAnnotationProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, String value, String dataTypeCurie) {
		OWLDatatype dataType = getOWLDataType(dataTypeCurie);
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value, dataType));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addAnnotationPropertyPlain(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, java.lang.String)
	 */
	@Override
	public void addAnnotationPropertyPlain(OWLIndividual subject,
			String predicateCurie, String value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value, ""));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addAnnotationPropertyPlain(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addAnnotationPropertyPlain(OWLIndividual subject,
			String predicateCurie, String value, String lang) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value, lang));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addDataProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, boolean)
	 */
	@Override
	public void addDataProperty(OWLIndividual s, String predicateCurie,
			boolean value) {
		addDataProperty(s, predicateCurie, factory.getOWLLiteral(value));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addDataProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, double)
	 */
	@Override
	public void addDataProperty(OWLIndividual s, String predicateCurie,
			double value) {
		addDataProperty(s, predicateCurie, factory.getOWLLiteral(value));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addDataProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, float)
	 */
	@Override
	public void addDataProperty(OWLIndividual s, String predicateCurie,
			float value) {
		addDataProperty(s, predicateCurie, factory.getOWLLiteral(value));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addDataProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, int)
	 */
	@Override
	public void addDataProperty(OWLIndividual s, String predicateCurie,
			int value) {
		addDataProperty(s, predicateCurie, factory.getOWLLiteral(value));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addDataProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, org.semanticweb.owlapi.model.OWLLiteral)
	 */
	@Override
	public void addDataProperty(OWLIndividual s, String predicateCurie,
			OWLLiteral literal) {
		OWLDataProperty dataProp = getOWLDataProperty(predicateCurie);

		OWLAxiom dataPropertyAssertion = factory
				.getOWLDataPropertyAssertionAxiom(dataProp, s, literal);
		manager.addAxiom(ontology, dataPropertyAssertion);
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addDataProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addDataProperty(OWLIndividual s, String predicateCurie,
			String value, String dataTypeCurie) {
		OWLDatatype dataType = getOWLDataType(dataTypeCurie);
		addDataProperty(s, predicateCurie,
				factory.getOWLLiteral(value, dataType));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addDataPropertyPlain(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, java.lang.String)
	 */
	@Override
	public void addDataPropertyPlain(OWLIndividual s, String predicateCurie,
			String plainLiteral) {
		addDataPropertyPlain(s, predicateCurie, plainLiteral, "");
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addDataPropertyPlain(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addDataPropertyPlain(OWLIndividual s, String predicateCurie,
			String plainLiteral, String lang) {
		addDataProperty(s, predicateCurie,
				factory.getOWLLiteral(plainLiteral, lang));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addObjectProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, org.semanticweb.owlapi.model.OWLIndividual)
	 */
	@Override
	public void addObjectProperty(OWLIndividual subject, String predicateCurie,
			OWLIndividual object) {
		OWLObjectProperty objProp = getOWLObjectProperty(predicateCurie);
		manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(
				objProp, subject, object));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addObjectProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String, java.lang.String)
	 */
	@Override
	public void addObjectProperty(OWLIndividual subject, String predicateCurie,
			String objectCurie) {
		addObjectProperty(subject, predicateCurie,
				getIndividual(objectCurie));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#addObjectProperty(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addObjectProperty(String subjectCurie, String predicateCurie, String objectCurie) {
		addObjectProperty(getIndividual(subjectCurie),
				predicateCurie, getIndividual(objectCurie));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#copyImports(org.semanticweb.owlapi.model.OWLOntology, org.semanticweb.owlapi.model.OWLOntology)
	 */
	@Override
	public void copyImports(OWLOntology src, OWLOntology dest) {
		for (IRI importDoc : src.getDirectImportsDocuments()) {
			if (! dest.getDirectImportsDocuments().contains(importDoc)) {
				importOntologyTo(importDoc, dest);
				// TODO: What if two different imports import the same ontology?
			}
		}
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#createIndividual(java.lang.String, java.lang.String)
	 */
	@Override
	public OWLNamedIndividual createIndividual(String individualCurie,
			String... typeCuries) {
		OWLNamedIndividual individual = factory
				.getOWLNamedIndividual(toIRI(individualCurie));
		if (isStrictIndividuals() && ontology.isDeclared(individual, true)) {
			throw new AlreadyExistsException(individualCurie);
		}

		manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(individual));

		for (String typeCurie : typeCuries) {
			OWLClass cls = getOWLClass(typeCurie);
			manager.addAxiom(ontology,
					factory.getOWLClassAssertionAxiom(cls, individual));
		}

		return individual;

	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#fillInOntology()
	 */
	@Override
	public void fillInOntology() {
		fillInOntology(null);
	}

	protected void fillInOntology(OWLOntology ontology, OWLReasoner reasoner) {
		if (reasoner == null) {
			reasoner = new StructuralReasonerFactory().createReasoner(ontology);
		}
		ArrayList<InferredAxiomGenerator<? extends OWLAxiom>> axiomGenerators = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		// Ignore axioms that would mainly tell us more about the imported
		// ontology
		// rather than our individuals, ie. only subclasses
		// of InferredIndividualAxiomGenerator
		axiomGenerators.add(new InferredClassAssertionAxiomGenerator());
		//axiomGenerators.add(new InferredPropertyAssertionGenerator());

		InferredOntologyGenerator generator = new InferredOntologyGenerator(
				reasoner, axiomGenerators);
		generator.fillOntology(getManager(), ontology);
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#fillInOntology(org.semanticweb.owlapi.reasoner.OWLReasoner)
	 */
	@Override
	public void fillInOntology(OWLReasoner reasoner) {
		// Fill in the imported ontologies first, as we don't want to 
		// save out clever stuff about them
		for (OWLOntology ont : getOntology().getImports()) {
			fillInOntology(ont, reasoner);
		}
		// Bud sadly the above did not work :(
		fillInOntology(getOntology(), reasoner);
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertiesBooleans(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public Set<Boolean> getDataPropertiesBooleans(OWLIndividual individual,
			String predicateCurie) {
		Set<Boolean> booleans = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			booleans.add(lit.parseBoolean());
		}
		return booleans;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertiesDoubles(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public Set<Double> getDataPropertiesDoubles(OWLIndividual individual,
			String predicateCurie) {
		Set<Double> doubles = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			doubles.add(lit.parseDouble());
		}
		return doubles;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertiesFloats(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public Set<Float> getDataPropertiesFloats(OWLIndividual individual,
			String predicateCurie) {
		Set<Float> floats = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			floats.add(lit.parseFloat());
		}
		return floats;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertiesIntegers(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public Set<Integer> getDataPropertiesIntegers(OWLIndividual individual,
			String predicateCurie) {
		Set<Integer> ints = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			ints.add(lit.parseInteger());
		}
		return ints;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertiesLiterals(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public Set<OWLLiteral> getDataPropertiesLiterals(OWLIndividual individual,
				String predicateCurie) {		
		return individual.getDataPropertyValues(
				getOWLDataProperty(predicateCurie), getOntology());
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertiesPlain(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public Set<String> getDataPropertiesPlain(OWLIndividual individual,
			String predicateCurie) {
		Set<String> strings = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			strings.add(lit.getLiteral());
		}
		return strings;
	}

	// TODO: Support for xsd:datetime/Calendar ?

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertyLiteral(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public OWLLiteral getDataPropertyLiteral(OWLIndividual individual,
			String predicateCurie) {
		Set<OWLLiteral> set = getDataPropertiesLiterals(individual, predicateCurie);
		return firstOfSet(set, individual + " " + predicateCurie + " *");		
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public String getDataProperty(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).getLiteral();
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertyPlain(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public String getDataPropertyPlain(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).getLiteral();
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertyBoolean(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public boolean getDataPropertyBoolean(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).parseBoolean();
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertyInteger(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public int getDataPropertyInteger(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).parseInteger();
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertyDouble(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public double getDataPropertyDouble(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).parseDouble();
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getDataPropertyFloat(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public float getDataPropertyFloat(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).parseFloat();
	}
	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getIndividual(java.lang.String)
	 */
	@Override
	public OWLNamedIndividual getIndividual(String individualCurie) {
		OWLNamedIndividual individual = factory
				.getOWLNamedIndividual(toIRI(individualCurie));
		if (isStrictIndividuals() && ! ontology.isDeclared(individual, true)) {
			throw new UnknownIndividualException(individualCurie);
		}
		return individual;		
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getIndividuals()
	 */
	@Override
	public Set<OWLNamedIndividual> getIndividuals() {
		return getOntology().getIndividualsInSignature();
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getIndividualsOfType(java.lang.String)
	 */
	@Override
	public Set<OWLIndividual> getIndividualsOfType(String typeCurie) {
		return getOWLClass(typeCurie).getIndividuals(getOntology());
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getManager()
	 */
	@Override
	public OWLOntologyManager getManager() {
		return manager;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getObjectProperties(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public Set<OWLIndividual> getObjectProperties(OWLIndividual individual,
			String predicateCurie) {
		OWLObjectProperty prop = getOWLObjectProperty(predicateCurie);
		return individual.getObjectPropertyValues(prop, getOntology());
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getObjectProperty(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public OWLIndividual getObjectProperty(OWLIndividual individual,
			String predicateCurie) {
		Set<OWLIndividual> set = getObjectProperties(individual, predicateCurie);
		return firstOfSet(set, individual + " " + predicateCurie + " *");
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getOntology()
	 */
	@Override
	public OWLOntology getOntology() {
		return ontology;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getOWLAnnotationProperty(java.lang.String)
	 */
	@Override
	public OWLAnnotationProperty getOWLAnnotationProperty(String predicateCurie) {
		OWLAnnotationProperty annProp = factory
				.getOWLAnnotationProperty(toIRI(predicateCurie));
		if (isStrictProperties() && !ontology.isDeclared(annProp, true)) {
			throw new UnknownAnnotationPropertyException(predicateCurie);
		}
		return annProp;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getOWLClass(java.lang.String)
	 */
	@Override
	public OWLClass getOWLClass(String typeCurie) {
		OWLClass cls = factory.getOWLClass(toIRI(typeCurie));
		System.out.println(cls);
		if (isStrictClasses() && !ontology.isDeclared(cls, true)) {
			throw new UnknownClassException(typeCurie);
		}
		return cls;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getOWLDataProperty(java.lang.String)
	 */
	@Override
	public OWLDataProperty getOWLDataProperty(String predicateCurie) {
		OWLDataProperty dataProp = factory
				.getOWLDataProperty(toIRI(predicateCurie));
		if (isStrictProperties() && !ontology.isDeclared(dataProp, true)) {
			throw new UnknownDataPropertyException(predicateCurie);
		}
		return dataProp;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getOWLDataType(java.lang.String)
	 */
	@Override
	public OWLDatatype getOWLDataType(String dataTypeCurie) {
		OWLDatatype dataType = factory.getOWLDatatype(toIRI(dataTypeCurie));
		if (isStrictDatatypes() && !dataType.isBuiltIn()
				&& !ontology.isDeclared(dataType, true)) {
			throw new UnknownDataTypeException(dataTypeCurie);
		}
		return dataType;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getOWLObjectProperty(java.lang.String)
	 */
	@Override
	public OWLObjectProperty getOWLObjectProperty(String predicateCurie) {
		OWLObjectProperty objProp = factory
				.getOWLObjectProperty(toIRI(predicateCurie));

		if (isStrictProperties() && !ontology.isDeclared(objProp, true)) {
			throw new UnknownObjectPropertyException(predicateCurie);
		}
		return objProp;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#getPrefixManager()
	 */
	@Override
	public DefaultPrefixManager getPrefixManager() {
		return prefixManager;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#importOntology(java.lang.String, java.lang.String)
	 */
	@Override
	public void importOntology(String prefix, String namespace)
			throws OWLOntologyCreationException {
		importOntology(prefix, namespace, namespace);
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#importOntology(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void importOntology(String prefix, String namespace,
			String loadFromIRI) throws OWLOntologyCreationException {
		IRI loadIRI = IRI.create(loadFromIRI);
		OWLOntology ont = manager.loadOntology(loadIRI);

		if (!prefix.endsWith(":")) {
			prefix = prefix + ":";
		}
		getPrefixManager().setPrefix(prefix, namespace);

		IRI importIRI;
		if (ont.isAnonymous()) {
			importIRI = loadIRI;
		} else {
			importIRI = ont.getOntologyID().getOntologyIRI();
		}
		importOntologyTo(importIRI, getOntology());
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#importOntologyTo(org.semanticweb.owlapi.model.IRI, org.semanticweb.owlapi.model.OWLOntology)
	 */
	@Override
	public void importOntologyTo(IRI importIRI, OWLOntology importingOntology) {
		OWLImportsDeclaration importDecl = factory
				.getOWLImportsDeclaration(importIRI);
		manager.applyChange(new AddImport(importingOntology, importDecl));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#isInstanceOf(org.semanticweb.owlapi.model.OWLIndividual, java.lang.String)
	 */
	@Override
	public boolean isInstanceOf(OWLIndividual individual, String string) {
		// TODO: Force subclass inferences etc?
		Set<OWLClassExpression> types = individual.getTypes(getOntology());
		return types.contains(getOWLClass("foaf:Person"));

	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#isStrict()
	 */
	@Override
	public boolean isStrict() {
		return isStrictClasses() || isStrictDatatypes() || isStrictIndividuals() || isStrictProperties();
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#isStrictClasses()
	 */
	@Override
	public boolean isStrictClasses() {
		return isStrictClasses;
	}
	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#isStrictDatatypes()
	 */
	@Override
	public boolean isStrictDatatypes() {
		return isStrictDatatypes;
	}
	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#isStrictIndividuals()
	 */
	@Override
	public boolean isStrictIndividuals() {
		return isStrictIndividuals;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#isStrictProperties()
	 */
	@Override
	public boolean isStrictProperties() {
		return isStrictProperties;
	}

	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#loadOntology(java.io.File)
	 */
	@Override
	public void loadOntology(File file) throws OWLOntologyCreationException {
		loadOntology(new FileDocumentSource(file));
	}
	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#loadOntology(java.io.InputStream)
	 */
	@Override
	public void loadOntology(InputStream inStream) throws OWLOntologyCreationException {		
		loadOntology(new StreamDocumentSource(inStream));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#loadOntology(java.io.InputStream, java.lang.String)
	 */
	@Override
	public void loadOntology(InputStream inStream, String ontologyIRI) throws OWLOntologyCreationException {		
		loadOntology(new StreamDocumentSource(inStream, IRI.create(ontologyIRI)));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#loadOntology(org.semanticweb.owlapi.io.OWLOntologyDocumentSource)
	 */
	@Override
	public void loadOntology(OWLOntologyDocumentSource src) throws OWLOntologyCreationException {
		OWLOntologyLoaderConfiguration loadConfig = new OWLOntologyLoaderConfiguration();
		for (IRI importDoc : getOntology().getDirectImportsDocuments()) {
			loadConfig.addIgnoredImport(importDoc);
			// Work around broken owl:imports due to 
			loadConfig.addIgnoredImport(importDoc.resolve("#null"));
			loadConfig.addIgnoredImport(importDoc.resolve("/null"));
		}
		
		OWLOntology ont = getManager().loadOntologyFromOntologyDocument(src, loadConfig);
		copyImports(getOntology(), ont);
		setOntology(ont);
	}
	

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#loadOntology(java.io.Reader)
	 */
	@Override
	public void loadOntology(Reader reader) throws OWLOntologyCreationException {		
		loadOntology(new ReaderDocumentSource(reader));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#loadOntology(java.io.Reader, java.lang.String)
	 */
	@Override
	public void loadOntology(Reader reader, String ontologyIRI) throws OWLOntologyCreationException {		
		loadOntology(new ReaderDocumentSource(reader, IRI.create(ontologyIRI)));
	}
	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#loadOntology(java.lang.String)
	 */
	@Override
	public void loadOntology(String ontologyIRI) throws OWLOntologyCreationException {
		loadOntology(new IRIDocumentSource(IRI.create(ontologyIRI)));
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#loadOntologyFromString(java.lang.String)
	 */
	@Override
	public void loadOntologyFromString(String ontologyStr) throws OWLOntologyCreationException {
		loadOntology(new StringDocumentSource(ontologyStr));
	}
	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#loadOntologyFromString(java.lang.String, java.lang.String)
	 */
	@Override
	public void loadOntologyFromString(String ontologyStr, String ontologyIRI) throws OWLOntologyCreationException {
		loadOntology(new StringDocumentSource(ontologyStr, IRI.create(ontologyIRI)));
	}
	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#saveOntology(java.io.OutputStream)
	 */
	@Override
	public void saveOntology(OutputStream outputStream)
			throws OWLOntologyStorageException {
		TurtleOntologyFormat turtleFormat = new TurtleOntologyFormat();
		turtleFormat.copyPrefixesFrom(getPrefixManager());
		// turtleFormat.setDefaultPrefix(ontologyIRI + "#");

		// try (OutputStream outputStream = Files.newOutputStream(testFile)) {
		manager.saveOntology(ontology, turtleFormat, outputStream);
		// }

	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#setManager(org.semanticweb.owlapi.model.OWLOntologyManager)
	 */
	@Override
	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
		this.factory = manager.getOWLDataFactory();
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#setOntology(org.semanticweb.owlapi.model.OWLOntology)
	 */
	@Override
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#setPrefixManager(org.semanticweb.owlapi.util.DefaultPrefixManager)
	 */
	@Override
	public void setPrefixManager(DefaultPrefixManager prefixManager) {
		this.prefixManager = prefixManager;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#setStrict(boolean)
	 */
	@Override
	public void setStrict(boolean isStrict) {
		setStrictClasses(isStrict);
		setStrictDatatypes(isStrict);
		setStrictIndividuals(isStrict);
		setStrictProperties(isStrict);
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#setStrictClasses(boolean)
	 */
	@Override
	public void setStrictClasses(boolean isStrictClasses) {
		this.isStrictClasses = isStrictClasses;
	}
	

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#setStrictDatatypes(boolean)
	 */
	@Override
	public void setStrictDatatypes(boolean isStrictDatatypes) {
		this.isStrictDatatypes = isStrictDatatypes;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#setStrictIndividuals(boolean)
	 */
	@Override
	public void setStrictIndividuals(boolean isStrictIndividuals) {
		this.isStrictIndividuals = isStrictIndividuals;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#setStrictProperties(boolean)
	 */
	@Override
	public void setStrictProperties(boolean isStrictProperties) {
		this.isStrictProperties = isStrictProperties;
	}
	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#toAnnotationSubject(org.semanticweb.owlapi.model.OWLObject)
	 */
	@Override
	public OWLAnnotationSubject toAnnotationSubject(OWLObject object) {
		OWLAnnotationSubject subject;
		if (object instanceof OWLAnnotationSubject) {
			subject = (OWLAnnotationSubject) object;
		} else if (object instanceof OWLNamedObject) {
			subject = ((OWLNamedObject) object).getIRI();
		} else {
			throw new IllegalArgumentException("Can't convert type "
					+ object.getClass() + " to OWLAnnotationSubject");
		}
		return subject;
	}

	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#toAnnotationValue(org.semanticweb.owlapi.model.OWLObject)
	 */
	@Override
	public OWLAnnotationValue toAnnotationValue(OWLObject object) {
		OWLAnnotationValue subject;
		if (object instanceof OWLAnnotationValue) {
			// This should cover both OWLLiteral and OWLAnonymousIndividual
			subject = (OWLAnnotationValue) object;
		} else if (object instanceof OWLNamedObject) {
			subject = ((OWLNamedObject) object).getIRI();
		} else {
			throw new IllegalArgumentException("Can't convert type "
					+ object.getClass() + " to OWLAnnotationValue");
		}
		return subject;
	}

	
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#toIRI(org.semanticweb.owlapi.model.OWLIndividual)
	 */
	@Override
	public String toIRI(OWLIndividual individual) {
		if (individual.isNamed()) {
			return individual.asOWLNamedIndividual().getIRI().toString();
		} else {
			throw new IllegalArgumentException("Not a named individual");
		}
	}
		
	/* (non-Javadoc)
	 * @see uk.org.mygrid.owlindividuals.impl.Individuals#toIRI(java.lang.String)
	 */
	@Override
	public IRI toIRI(String curie) {
		if (curie.startsWith("<") && curie.endsWith(">")) {
			return IRI.create(curie.substring(1, curie.length() - 1));
		}
		return getPrefixManager().getIRI(curie);
	}

	// TODO set* methods
	// TODO clear* methods

}
