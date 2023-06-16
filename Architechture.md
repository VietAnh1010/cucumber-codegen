# Architechture of Cucumber JVM

### Written in this file are my observations about the architechture of Cucumner JVM

#### Feature Parser (io.cucumber.core.feature)

- Load all possible parsers.
- Compares the parsers using their versions.
- Choose the lastest parser, and parse the resource with that parser.
- TODO: look at the gherkin parser and understand its logic.

#### FeaturePathFeatureParser (io.cucumber.core.runtime)

- Take an instance of `RuntimeOptions` as dependency.
- Delegate most of the work to `ResourceScanner`.

#### Feature files should be located in /resource folder

#### Execution Context

- Responsible for running and collecting the test cases
- Works by creating one runner for each test case, and then run the runner with the test case.
- Afterward, collect all information about the test cases, including the errors occured during the test cases
- Order of called methods:
    - startTestRun          -> emit new information at the start of the tests
    - runBeforeAllHooks     -> run the global @BeforeAll
    - executeFeatures.run() -> run each test cases (delegate to the runner)
    - runAfterAllHooks      -> the the global @AfterAll
    - finishTestRun         -> emit information about the end of the test

#### Runner 

- Depends on a number of backends
- Depends on a single object factory (to create mock objects during the test cases, I guess?)
- For each pickle, when run:
    - First, create a step type registry for the pickle
    - Step type registry is just the composition of 3 classes: parameter type, data table type and doc string type (registry)
    - Facade pattern
        - Create parameter type registry
            - The float RegExp is kept generic, and is substituted by actual values based on the locale
            - TODO: understand ParameterByTypeTransformer
            - Define the default and the internal parameter transformer
            - Define parameter type for Java built-in types:
                - biginteger
                - bigdecimal
                - byte
                - short
                - int
                - long
                - float
                - double
                - word
                - string
                - anonymous (wildcard pattern). Name of this parameter type is an empty string    
                - Define a mapping between parameter types and their names
                - Define a mapping between parameter types and their corresponding regex
            - This parameter type registry can be used to look up the parameter type based on their names/their regexes
        - Create data table type registry
        - Create doc string type registry
    - Then, perpare the snippet generator
    - Then, build the backend world
    - Then, prepare the glue for the test case
    - Then, create and run the test case
        - For each test cases, if there is no corresponding matching, then a suggestion will be created and emitted through the event bus
        - TODO: read the snippet generator and understand its logic
    - Then, remove the glue
    - Then, destroy the backend world
    - TODO: Understand the logic of the glue

#### Event Bus
- Not necessary for our purpose right now

#### Type Parameter
- Contains the relevant information about the parameter type inside Cucumber
    - Name of the type
    - The type to transform the parsed string into (this is actually stored in the type parameter T)
    - NOTE: This type will be exposed to outside methods
    - The regexes that match this parameter type
    - And some other, not important boolean flags

#### Snippet
- Comes from the backend

#### Java Backend (use annotation to setup the test cases)
- Return Java Snippet: just the template for snippet generator

#### Glue
- Responsible for loading the steps and hooks
- The only implementation is Caching Glue

#### Caching Glue

#### Glue Adaptor
- A facade class for the glue, simplify the logic to setup a new glue.

#### Classpath support
- Is a utitily class, contains only static methods
- Just various utilities to transform between strings and uris for locating resources

#### Classpath Scanner
- Scan for class files in a package, identified bt the package URI or the package name (represented as a String)
- Load all classes, and put them into the JVM
- If encounter a jar file, then we shall unzip the jar file to a separate file system and traverse inside that file system. After the traversal complates, we will atomatically close this file system

#### Path Scanner
- Walk the file system - a bridge between the java core library and cucumber

#### Method Scanner
- Utility class
- 

#### Look Up (interface, implemented by ObjectFactory)
- Allow lookup for objects?

#### Container (interface, implemented by ObjectFactory)
- Allow to store new objects?

#### Java 8 Backend (use lambda to setup the test cases)
- TODO: read this again to fully understand the architechture

#### Backend Service Loader (implements Backend Supplier)
- A supplier of backend instances
- Use `java.util.ServiceLoader` to find and load instances of `Backend`

#### Snippet Generator
- Contains a snippet and a generator to generate the snippets
- The generator depends on type parameter registry
- A list of expression will be generated from the text of the step
- 2 identifier generator will be created to generate the name of the function and parameters
- For each expression, we will generate multiple snippets (indicated by the return type `List<String>`)

#### TODO
- Feature scanner (each feature is corresponded to a single feature file)
- Glue scanner (we need to know the class file where the glue comes from)
- Validation
- Suggested Step / Pickle / Feature should contains only pure data. We will write another class to handle the conversion between them
- Do we allow support for multiple backend? To handle multiple features?

