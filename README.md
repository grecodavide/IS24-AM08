# Project Creation
The project was created by running
```
mvn archetype:generate \
    -DgroupId=com.is.proj.group8 \
    -DartifactId=IS24-AM08 \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DarchetypeVersion=1.4 \
    -DinteractiveMode=false

```

Flags:
- `DgroupId`: unique base name of the company who created the project (`com.is.proj.group8`)
- `DartifactId`: unique name of the project (`IS24-AM08`)
- `DarchetypeArtifactId`: base struct of project


To ensure we all run the same maven version (so that if it compiles on one machine, it'll do so on all the others), we also
use `mvnw`, maven wrapper. This way, all the commands we have to run (`mvn build`, `mvn package`, `mvn test`...) will instead be run with `mvnw` 
(`./mvnw build` ... on linux/macOS, `.\mvnw.cmd` on Windows)
