# order-service (consumes dto proto schemas, generates Java locally)

This service demonstrates consumption from custom dto library(from seperate maven project):
- The commons repo publishes `.proto` files only.
- The service pulls the schema JAR, unpacks `.proto` files, then runs `protoc` locally to generate Java.

## 1) Build/install schema repo locally
```bash
cd ../commons
mvn -q clean install
```

## 2) Build this service
```bash
cd ../order-service
mvn -q clean package
```

## 3) Run
```bash
java -jar target/order-service-1.0.0.jar
```

`.proto` files are unpacked into:
- `target/proto/`

Generated proto sources land in:
- `target/generated-sources/protobuf/java/`
