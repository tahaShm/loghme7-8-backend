1. first install maven (mvn) and set its variable paths: https://howtodoinjava.com/maven/how-to-install-maven-on-windows/
2. add this in your pom file: build/plugins : https://mkyong.com/docker/docker-spring-boot-examples/
	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-compiler-plugin</artifactId>
		<version>3.8.0</version>
		<configuration>
			<source>${java.version}</source>
			<target>${java.version}</target>
		</configuration>
	</plugin>
and set ${java.version} to correct java version
3. continue to make docker file and image with this link: https://mkyong.com/docker/docker-spring-boot-examples/
note: rename the .jar file to yours.