# commons (dto proto schemas)

This repo publishes a Maven artifact that contains only `.proto` files (no generated code):
- pc.mvn.pkg:dto:0.0.1

It also provides an optional BOM:
- pc.mvn.pkg:bom:0.0.1

## install - Publishes package to Local (installs into ~/.m2)
```bash
mvn -q clean install
```

## deploy - Publishes package to Repository (Nexus/Artifactory)
1) Configure `distributionManagement` in the parent POM (already included as an example).
2) Provide credentials in `~/.m2/settings.xml` (see `sample-settings-repository.xml`).
3) Run:
```bash
mvn -q -DskipTests deploy
```

## compat - Profile to Compatibility check (japicmp)
The build includes a profile `compat` that can compare this release against a previous released version:
```bash
mvn -Pcompat -DpreviousVersion=0.0.0 verify
```
Notes:
- For schema-only JARs, binary compatibility is less meaningful than schema compatibility,
  but this is useful if you also publish generated code or API jars in the future.
- If you remain schema-only, consider adding schema linting (e.g., buf) in CI.
