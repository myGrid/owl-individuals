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
import uk.org.mygrid.owlindividuals.api.MultipleFoundException;
import uk.org.mygrid.owlindividuals.api.UnknownAnnotationPropertyException;
import uk.org.mygrid.owlindividuals.api.UnknownClassException;
import uk.org.mygrid.owlindividuals.api.UnknownDataPropertyException;
import uk.org.mygrid.owlindividuals.api.UnknownDataTypeException;
import uk.org.mygrid.owlindividuals.api.UnknownIndividualException;
import uk.org.mygrid.owlindividuals.api.UnknownObjectPropertyException;

public class Individuals {

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

	public Individuals() throws OWLOntologyCreationException {
		setManager(OWLManager.createOWLOntologyManager());
		setOntology(getManager().createOntology());
	}

	public Individuals(OWLOntologyManager manager, OWLOntology ontology) {
		this.setManager(manager);
		this.setOntology(ontology);
	}

	public void addAnnotationProperty(OWLAnnotationSubject subject,
			String predicateCurie, OWLAnnotationValue value) {
		OWLAnnotationProperty annProp = getOWLAnnotationProperty(predicateCurie);
		manager.addAxiom(ontology,
				factory.getOWLAnnotationAssertionAxiom(annProp, subject, value));
	}
	
	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, boolean value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value));
	}

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, double value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value));
	}

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, float value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value));
	}

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, int value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value));
	}

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, OWLIndividual object) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				toAnnotationValue(object));
	}

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, String value, String dataTypeCurie) {
		OWLDatatype dataType = getOWLDataType(dataTypeCurie);
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value, dataType));
	}

	public void addAnnotationPropertyPlain(OWLIndividual subject,
			String predicateCurie, String value) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value, ""));
	}

	public void addAnnotationPropertyPlain(OWLIndividual subject,
			String predicateCurie, String value, String lang) {
		addAnnotationProperty(toAnnotationSubject(subject), predicateCurie,
				factory.getOWLLiteral(value, lang));
	}

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			boolean value) {
		addDataProperty(s, predicateCurie, factory.getOWLLiteral(value));
	}

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			double value) {
		addDataProperty(s, predicateCurie, factory.getOWLLiteral(value));
	}

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			float value) {
		addDataProperty(s, predicateCurie, factory.getOWLLiteral(value));
	}

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			int value) {
		addDataProperty(s, predicateCurie, factory.getOWLLiteral(value));
	}

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			OWLLiteral literal) {
		OWLDataProperty dataProp = getOWLDataProperty(predicateCurie);

		OWLAxiom dataPropertyAssertion = factory
				.getOWLDataPropertyAssertionAxiom(dataProp, s, literal);
		manager.addAxiom(ontology, dataPropertyAssertion);
	}

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			String value, String dataTypeCurie) {
		OWLDatatype dataType = getOWLDataType(dataTypeCurie);
		addDataProperty(s, predicateCurie,
				factory.getOWLLiteral(value, dataType));
	}

	public void addDataPropertyPlain(OWLIndividual s, String predicateCurie,
			String plainLiteral) {
		addDataPropertyPlain(s, predicateCurie, plainLiteral, "");
	}

	public void addDataPropertyPlain(OWLIndividual s, String predicateCurie,
			String plainLiteral, String lang) {
		addDataProperty(s, predicateCurie,
				factory.getOWLLiteral(plainLiteral, lang));
	}

	/**
	 * Add an object property to an individual
	 * 
	 * @param subject
	 *            The subject individual
	 * @param predicateCurie
	 *            A CURIE or &lt;URI&gt; identifying the property
	 * @param object
	 *            The object individual
	 * @throws UnknownObjectPropertyException
	 *             if {@link #isStrict()} and the object property is not defined
	 *             in any of the imported ontologies. (Note that this might be
	 *             because it is an annotation property, in which case you need
	 *             to use addAnnotationProperty).
	 */
	public void addObjectProperty(OWLIndividual subject, String predicateCurie,
			OWLIndividual object) {
		OWLObjectProperty objProp = getOWLObjectProperty(predicateCurie);
		manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(
				objProp, subject, object));
	}

	public void addObjectProperty(OWLIndividual subject, String predicateCurie,
			String objectCurie) {
		addObjectProperty(subject, predicateCurie,
				getIndividual(objectCurie));
	}

	public void addObjectProperty(String subjectCurie, String predicateCurie, String objectCurie) {
		addObjectProperty(getIndividual(subjectCurie),
				predicateCurie, getIndividual(objectCurie));
	}

	public void copyImports(OWLOntology src, OWLOntology dest) {
		for (IRI importDoc : src.getDirectImportsDocuments()) {
			if (! dest.getDirectImportsDocuments().contains(importDoc)) {
				importOntologyTo(importDoc, dest);
				// TODO: What if two different imports import the same ontology?
			}
		}
	}

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

	public void fillInOntology(OWLReasoner reasoner) {
		// Fill in the imported ontologies first, as we don't want to 
		// save out clever stuff about them
		for (OWLOntology ont : getOntology().getImports()) {
			fillInOntology(ont, reasoner);
		}
		// Bud sadly the above did not work :(
		fillInOntology(getOntology(), reasoner);
	}

	public Set<Boolean> getDataPropertiesBooleans(OWLIndividual individual,
			String predicateCurie) {
		Set<Boolean> booleans = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			booleans.add(lit.parseBoolean());
		}
		return booleans;
	}

	public Set<Double> getDataPropertiesDoubles(OWLIndividual individual,
			String predicateCurie) {
		Set<Double> doubles = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			doubles.add(lit.parseDouble());
		}
		return doubles;
	}

	public Set<Float> getDataPropertiesFloats(OWLIndividual individual,
			String predicateCurie) {
		Set<Float> floats = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			floats.add(lit.parseFloat());
		}
		return floats;
	}

	public Set<Integer> getDataPropertiesIntegers(OWLIndividual individual,
			String predicateCurie) {
		Set<Integer> ints = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			ints.add(lit.parseInteger());
		}
		return ints;
	}

	public Set<OWLLiteral> getDataPropertiesLiterals(OWLIndividual individual,
				String predicateCurie) {		
		return individual.getDataPropertyValues(
				getOWLDataProperty(predicateCurie), getOntology());
	}

	public Set<String> getDataPropertiesPlain(OWLIndividual individual,
			String predicateCurie) {
		Set<String> strings = new LinkedHashSet<>();
		for (OWLLiteral lit : getDataPropertiesLiterals(individual, predicateCurie)) {
			strings.add(lit.getLiteral());
		}
		return strings;
	}

	// TODO: Support for xsd:datetime/Calendar ?

	public OWLLiteral getDataPropertyLiteral(OWLIndividual individual,
			String predicateCurie) {
		Set<OWLLiteral> set = getDataPropertiesLiterals(individual, predicateCurie);
		return firstOfSet(set, individual + " " + predicateCurie + " *");		
	}

	public String getDataProperty(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).getLiteral();
	}

	public String getDataPropertyPlain(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).getLiteral();
	}

	public boolean getDataPropertyBoolean(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).parseBoolean();
	}

	public int getDataPropertyInteger(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).parseInteger();
	}

	public double getDataPropertyDouble(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).parseDouble();
	}

	public float getDataPropertyFloat(OWLIndividual individual,
			String predicateCurie) {
		return getDataPropertyLiteral(individual, predicateCurie).parseFloat();
	}
	
	public OWLNamedIndividual getIndividual(String individualCurie) {
		OWLNamedIndividual individual = factory
				.getOWLNamedIndividual(toIRI(individualCurie));
		if (isStrictIndividuals() && ! ontology.isDeclared(individual, true)) {
			throw new UnknownIndividualException(individualCurie);
		}
		return individual;		
	}

	public Set<OWLNamedIndividual> getIndividuals() {
		return getOntology().getIndividualsInSignature();
	}

	public Set<OWLIndividual> getIndividualsOfType(String typeCurie) {
		return getOWLClass(typeCurie).getIndividuals(getOntology());
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public Set<OWLIndividual> getObjectProperties(OWLIndividual individual,
			String predicateCurie) {
		OWLObjectProperty prop = getOWLObjectProperty(predicateCurie);
		return individual.getObjectPropertyValues(prop, getOntology());
	}

	public OWLIndividual getObjectProperty(OWLIndividual individual,
			String predicateCurie) {
		Set<OWLIndividual> set = getObjectProperties(individual, predicateCurie);
		return firstOfSet(set, individual + " " + predicateCurie + " *");
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public OWLAnnotationProperty getOWLAnnotationProperty(String predicateCurie) {
		OWLAnnotationProperty annProp = factory
				.getOWLAnnotationProperty(toIRI(predicateCurie));
		if (isStrictProperties() && !ontology.isDeclared(annProp, true)) {
			throw new UnknownAnnotationPropertyException(predicateCurie);
		}
		return annProp;
	}

	public OWLClass getOWLClass(String typeCurie) {
		OWLClass cls = factory.getOWLClass(toIRI(typeCurie));
		System.out.println(cls);
		if (isStrictClasses() && !ontology.isDeclared(cls, true)) {
			throw new UnknownClassException(typeCurie);
		}
		return cls;
	}

	public OWLDataProperty getOWLDataProperty(String predicateCurie) {
		OWLDataProperty dataProp = factory
				.getOWLDataProperty(toIRI(predicateCurie));
		if (isStrictProperties() && !ontology.isDeclared(dataProp, true)) {
			throw new UnknownDataPropertyException(predicateCurie);
		}
		return dataProp;
	}

	public OWLDatatype getOWLDataType(String dataTypeCurie) {
		OWLDatatype dataType = factory.getOWLDatatype(toIRI(dataTypeCurie));
		if (isStrictDatatypes() && !dataType.isBuiltIn()
				&& !ontology.isDeclared(dataType, true)) {
			throw new UnknownDataTypeException(dataTypeCurie);
		}
		return dataType;
	}

	public OWLObjectProperty getOWLObjectProperty(String predicateCurie) {
		OWLObjectProperty objProp = factory
				.getOWLObjectProperty(toIRI(predicateCurie));

		if (isStrictProperties() && !ontology.isDeclared(objProp, true)) {
			throw new UnknownObjectPropertyException(predicateCurie);
		}
		return objProp;
	}

	public DefaultPrefixManager getPrefixManager() {
		return prefixManager;
	}

	public void importOntology(String prefix, String namespace)
			throws OWLOntologyCreationException {
		importOntology(prefix, namespace, namespace);
	}

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

	public void importOntologyTo(IRI importIRI, OWLOntology importingOntology) {
		OWLImportsDeclaration importDecl = factory
				.getOWLImportsDeclaration(importIRI);
		manager.applyChange(new AddImport(importingOntology, importDecl));
	}

	public boolean isInstanceOf(OWLIndividual individual, String string) {
		// TODO: Force subclass inferences etc?
		Set<OWLClassExpression> types = individual.getTypes(getOntology());
		return types.contains(getOWLClass("foaf:Person"));

	}

	public boolean isStrict() {
		return isStrictClasses() || isStrictDatatypes() || isStrictIndividuals() || isStrictProperties();
	}

	public boolean isStrictClasses() {
		return isStrictClasses;
	}
	
	public boolean isStrictDatatypes() {
		return isStrictDatatypes;
	}
	
	public boolean isStrictIndividuals() {
		return isStrictIndividuals;
	}

	public boolean isStrictProperties() {
		return isStrictProperties;
	}

	
	public void loadOntology(File file) throws OWLOntologyCreationException {
		loadOntology(new FileDocumentSource(file));
	}
	
	public void loadOntology(InputStream inStream) throws OWLOntologyCreationException {		
		loadOntology(new StreamDocumentSource(inStream));
	}

	public void loadOntology(InputStream inStream, String ontologyIRI) throws OWLOntologyCreationException {		
		loadOntology(new StreamDocumentSource(inStream, IRI.create(ontologyIRI)));
	}

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
	

	public void loadOntology(Reader reader) throws OWLOntologyCreationException {		
		loadOntology(new ReaderDocumentSource(reader));
	}

	public void loadOntology(Reader reader, String ontologyIRI) throws OWLOntologyCreationException {		
		loadOntology(new ReaderDocumentSource(reader, IRI.create(ontologyIRI)));
	}
	
	public void loadOntology(String ontologyIRI) throws OWLOntologyCreationException {
		loadOntology(new IRIDocumentSource(IRI.create(ontologyIRI)));
	}

	public void loadOntologyFromString(String ontologyStr) throws OWLOntologyCreationException {
		loadOntology(new StringDocumentSource(ontologyStr));
	}
	
	public void loadOntologyFromString(String ontologyStr, String ontologyIRI) throws OWLOntologyCreationException {
		loadOntology(new StringDocumentSource(ontologyStr, IRI.create(ontologyIRI)));
	}
	
	public void saveOntology(OutputStream outputStream)
			throws OWLOntologyStorageException {
		TurtleOntologyFormat turtleFormat = new TurtleOntologyFormat();
		turtleFormat.copyPrefixesFrom(getPrefixManager());
		// turtleFormat.setDefaultPrefix(ontologyIRI + "#");

		// try (OutputStream outputStream = Files.newOutputStream(testFile)) {
		manager.saveOntology(ontology, turtleFormat, outputStream);
		// }

	}

	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
		this.factory = manager.getOWLDataFactory();
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	public void setPrefixManager(DefaultPrefixManager prefixManager) {
		this.prefixManager = prefixManager;
	}

	public void setStrict(boolean isStrict) {
		setStrictClasses(isStrict);
		setStrictDatatypes(isStrict);
		setStrictIndividuals(isStrict);
		setStrictProperties(isStrict);
	}

	public void setStrictClasses(boolean isStrictClasses) {
		this.isStrictClasses = isStrictClasses;
	}
	

	public void setStrictDatatypes(boolean isStrictDatatypes) {
		this.isStrictDatatypes = isStrictDatatypes;
	}

	public void setStrictIndividuals(boolean isStrictIndividuals) {
		this.isStrictIndividuals = isStrictIndividuals;
	}

	public void setStrictProperties(boolean isStrictProperties) {
		this.isStrictProperties = isStrictProperties;
	}
	
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

	
	public String toIRI(OWLIndividual individual) {
		if (individual.isNamed()) {
			return individual.asOWLNamedIndividual().getIRI().toString();
		} else {
			throw new IllegalArgumentException("Not a named individual");
		}
	}
		
	public IRI toIRI(String curie) {
		if (curie.startsWith("<") && curie.endsWith(">")) {
			return IRI.create(curie.substring(1, curie.length() - 1));
		}
		return getPrefixManager().getIRI(curie);
	}

	// TODO set* methods
	// TODO clear* methods

}
