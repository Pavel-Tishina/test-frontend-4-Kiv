<h1>test4sandbox</h1>
This application using next tech-stack:

- Spring Boot 3.2.5
- JDK17
- Hibernate
- H2 database (create db-file)
- NodeJS 20.12.2
- React 18.3.1
- React Bootstrap 2.10.2
- axios (http client)



<h2>Prerequisites</h2>

Please check first next applications:
- docker (tested on Windows10 with <a hred="https://www.docker.com/products/docker-desktop/">Docker Desktop</a>)
- maven 3.9.1 and <a href="https://maven.apache.org/download.cgi">above</a>
- jdk 17.0.5 and above (application develop and tested with <a href=" https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html">Amazon Corretto JDK 17.0.5</a>)
- have opened and free port **8080** and **3000** 

Amazon Corretto 17.0.5 distr can find here:
    Windows: https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.zip
    Linux:   https://corretto.aws/downloads/latest/amazon-corretto-17-x64-linux-jdk.tar.gz
    MacOS:  https://corretto.aws/downloads/latest/amazon-corretto-17-aarch64-macos-jdk.tar.gz

Common distributive download page is
https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html

Please check your **JAVA_HOME** and **MAVEN_HOME**, run commands such
**java -version** and **mvn -version** and make sure that all working fine

<h2>How to run</h2>

1. clone a project repository to your repository root folder 
<p><code>git clone https://github.com/Pavel-Tishina/test4sandbox.git</code></p>

2. go to the Fontend catalogue
<p><code>cd ./test4sandbox/app</code></p>

3. build docker image for **frontend** application 
<p><code>docker build -t test4sandbox-frontend ./</code></p>

4. go back to the backend catalogue
<p><code>cd ..</code></p>

5. compile project
<p><code>mvn clean package</code></p>

6. build docker image for **backend** application
<p><code>docker build -t test4sandbox ./</code></p>

7. run images (in 2 different terminals)
<div><b>backend:</b>
<code>docker run -p 8080:8080 test4sandbox</code>
</div>

<div><b>frontend</b>
<code>docker run -p 3000:3000 test4sandbox-frontend</code>
</div>


8. check running of backend application (it must return a not empty JSON-response)
<p><code>curl -X GET "http://localhost:8080/rest/api/employee/page?p=0&lim=5"</code></p>
example of JSON-response:
<code>{"result":[{"lastName":"Doe","firstName":"John","created":"2024-05-03 00:41:04","fullName":"John Doe (1)","id":"1","position":"Manager","supervisorFullName":"","supervisor":""},{"lastName":"Smith","firstName":"Jane","created":"2024-05-03 00:41:04","fullName":"Jane Smith (2)","id":"2","position":"Assistant Manager","supervisorFullName":"John Doe (1)","supervisor":"1"},{"lastName":"Johnson","firstName":"Alice","created":"2024-05-03 00:41:04","fullName":"Alice Johnson (3)","id":"3","position":"Team Leader","supervisorFullName":"John Doe (1)","supervisor":"1"}]}</code>

9. open frontend-app in browser and start working with gui
<p><code>http://localhost:3000</code></p>

<h3>Alternative way without docker</h3>
1. clone a project repository to your repository root folder
<p><code>git clone https://github.com/Pavel-Tishina/test4sandbox.git</code></p>

2. open in IDE as maven-project and start with <b>Test4sandboxApplication.java</b>
3. check running of backend application (it must return a not empty JSON-response)
<p><code>curl -X GET "http://localhost:8080/rest/api/employee/page?p=0&lim=5"</code></p>
example of JSON-response:
<code>{"result":[{"lastName":"Doe","firstName":"John","created":"2024-05-03 00:41:04","fullName":"John Doe (1)","id":"1","position":"Manager","supervisorFullName":"","supervisor":""},{"lastName":"Smith","firstName":"Jane","created":"2024-05-03 00:41:04","fullName":"Jane Smith (2)","id":"2","position":"Assistant Manager","supervisorFullName":"John Doe (1)","supervisor":"1"},{"lastName":"Johnson","firstName":"Alice","created":"2024-05-03 00:41:04","fullName":"Alice Johnson (3)","id":"3","position":"Team Leader","supervisorFullName":"John Doe (1)","supervisor":"1"}]}</code>


4. go to the Fontend catalogue
<p><code>cd ./test4sandbox/app</code></p>

5. install dependency (run command in terminal)
<p><code>npm i</code></p>

6. run frontend (run command in terminal)
<p><code>npm start</code></p>

7. open frontend-app in browser and start working with gui
<p><code>http://localhost:3000</code></p>
