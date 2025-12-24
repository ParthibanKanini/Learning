# Maven Multi-module sample: Commons proto-only + microservice consumer 

This bundle contains two independent projects (separate repos):

1) commons/
   - Sub-modules
      - Publishes schema-only proto artifact: pc.mvn.pkg:dto:0.0.1
      - Optional BOM package: pc.mvn.pkg:bom:0.0.1
   - Includes example `distributionManagement` for Nexus/Artifactory
   - Includes a compatibility profile pattern (japicmp)

2) order-service/
   - Consumes dto proto schema artifact
   - Unpacks `.proto` files from the dependency into `target/dto`
   - Runs protoc locally to generate Java
   - Builds an executable jar

## Run end-to-end
```bash
cd commons
mvn -q clean install

cd ../order-service
mvn -q clean package
java -jar target/order-service-1.0.0.jar
```
### To ensure the IDE errors are avoided.
```bash
mvn clean compile

```


## Features Supported

### Commons Module

#### 1. **Multi-Module Project Structure**
   - Aggregated parent POM with module management
   - Centralized dependency and plugin management via `pluginManagement`
   - Clear separation of concerns (dto, bom sub-modules)

#### 2. **Protocol Buffers (Protobuf) Support**
   - Proto file definitions for domain models
   - Support for multiple proto packages: `common`, `inventory`, `order`
   - Proto schema versioning and distribution

#### 3. **Artifact Repository Management**
   - Configured `distributionManagement` for Nexus/Artifactory deployment
   - Support for both release and snapshot repositories
   - Credentials management via `~/.m2/settings.xml`

#### 4. **Build Consistency & Quality Gates**
   - **Maven Enforcer Plugin**: Enforces build rules and prevents common mistakes
     - Validates Maven and Java versions
     - Can enforce dependency convergence
     - Prevents SNAPSHOT dependencies in releases
   - **Centralized Plugin Management**: All child modules inherit plugin versions

#### 5. **Java API Compatibility Checking**
   - **JApiCmp Plugin** (via `compat` profile): Validates backward compatibility
     - Detects breaking API changes between versions
     - Ensures semantic versioning compliance
     - Profile-based activation for controlled compatibility checks
     - Usage: `mvn clean verify -P compat -DpreviousVersion=0.0.3`

#### 6. **Standardized Build Properties**
   - UTF-8 character encoding across all modules
   - Java 21 LTS target version
   - Version management for tools and dependencies

### Order Service Module

#### 1. **Protocol Buffer Code Generation Pipeline**
   - **Step 1 - Unpack**: Maven Dependency Plugin extracts `.proto` files from schema JAR
   - **Step 2 - Compile**: Protobuf Maven Plugin generates Java classes from proto definitions
   - **Step 2b - Registration**: Build Helper Plugin registers generated sources with Maven and IDEs
   - **Step 3 - Compile Java**: Maven Compiler Plugin compiles both hand-written and generated code
   - **Step 4 - Package**: JAR Plugin creates the application JAR with manifest configuration

#### 2. **Cross-Platform Protobuf Compilation**
   - **OS Maven Plugin**: Automatic OS detection and classifier generation
   - Downloads appropriate protoc binary for current OS (Windows, Linux, macOS)
   - Supports ARM64 and x86_64 architectures
   - Enables reproducible builds across different developer machines

#### 3. **Schema-Driven Microservice Architecture**
   - Decoupled schema consumption from service implementation
   - Services depend on external schema artifact, not co-located proto files
   - Independent versioning of schemas and service implementations
   - Multiple service versions can share the same schema version

#### 4. **Executable JAR with Dependencies**
   - **Maven Shade Plugin**: Creates fat/uber JAR with all dependencies bundled
   - Single JAR deployment: `java -jar order-service-1.0.0.jar`
   - Proper resource and metadata merging via transformers
   - Preserves main class configuration in manifest

#### 5. **IDE Integration & Source Management**
   - Generated sources properly registered as source roots
   - IDE error checking works correctly for generated classes
   - Separation of generated sources from hand-written code
   - Prevents false "cannot find symbol" errors in VS Code/Eclipse

#### 6. **Build Quality & Enforcement**
   - Maven Enforcer Plugin with execution rules
   - Requires Maven 3.8.6+ (security fixes for HTTPS-only repositories)
   - Requires Java 21+ runtime compatibility
   - Prevents accidental SNAPSHOT dependencies in builds
   - Fail-fast approach: stops build immediately on rule violations

#### 7. **Java Compilation Strictness**
   - Java 21 as both source and target version (using `release` option)
   - Comprehensive compiler warnings enabled (`-Xlint:all`)
   - Ensures compatibility with Java 21 runtime libraries
