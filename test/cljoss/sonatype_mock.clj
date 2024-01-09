(ns cljoss.sonatype-mock
  (:require
   [clojure.data.json :as json]
   [org.httpkit.server :as httpkit]))

(def ^:private reports 
  "Reports for the following three packages
   obtained on 2024-01-02
   
   - org/apache/xmlgraphics/batik-css/1.16/batik-css-1.16.jar
   - org/eclipse/jetty/jetty-util/9.4.48.v20220622/jetty-util-9.4.48.v20220602.jar
   - mysql/mysql-connector-java/8.0.16/mysql-connector-java-8.0.16.jar"
  [{"coordinates" "pkg:maven/org.apache.xmlgraphics/batik-css@1.16", 
    "description" "",
    "reference"
    "https://ossindex.sonatype.org/component/pkg:maven/org.apache.xmlgraphics/batik-css@1.16?utm_source=apache-httpclient&utm_medium=integration&utm_content=4.5.13",
    "vulnerabilities" []}
   {"coordinates" "pkg:maven/org.eclipse.jetty/jetty-util@9.4.48.v20220622",
    "description" "Utility classes for Jetty",
    "reference"
    "https://ossindex.sonatype.org/component/pkg:maven/org.eclipse.jetty/jetty-util@9.4.48.v20220622?utm_source=apache-httpclient&utm_medium=integration&utm_content=4.5.13",
    "vulnerabilities"
    [{"cvssVector" "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:N/I:N/A:L",
      "cvssScore" 5.3,
      "displayName" "CVE-2023-26048",
      "id" "CVE-2023-26048",
      "reference"
      "https://ossindex.sonatype.org/vulnerability/CVE-2023-26048?component-type=maven&component-name=org.eclipse.jetty%2Fjetty-util&utm_source=apache-httpclient&utm_medium=integration&utm_content=4.5.13",
      "title" "[CVE-2023-26048] CWE-400: Uncontrolled Resource Consumption ('Resource Exhaustion')",
      "cve" "CVE-2023-26048",
      "externalReferences"
      ["http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2023-26048"
       "https://github.com/eclipse/jetty.project/pull/9344"
       "https://github.com/eclipse/jetty.project/pull/9345"
       "https://github.com/eclipse/jetty.project/security/advisories/GHSA-qw69-rqj8-6qw8"],
      "description"
      "Jetty is a java based web server and servlet engine. In affected versions servlets with multipart support (e.g. annotated with `@MultipartConfig`) that call `HttpServletRequest.getParameter()` or `HttpServletRequest.getParts()` may cause `OutOfMemoryError` when the client sends a multipart request with a part that has a name but no filename and very large content. This happens even with the default settings of `fileSizeThreshold=0` which should stream the whole part content to disk. An attacker client may send a large multipart request and cause the server to throw `OutOfMemoryError`. However, the server may be able to recover after the `OutOfMemoryError` and continue its service -- although it may take some time. This issue has been patched in versions 9.4.51, 10.0.14, and 11.0.14. Users are advised to upgrade. Users unable to upgrade may set the multipart parameter `maxRequestSize` which must be set to a non-negative value, so the whole multipart content is limited (although still read into memory).",
      "cwe" "CWE-400"}]}
   {"coordinates" "pkg:maven/mysql/mysql-connector-java@8.0.16",
    "description" "MySQL java connector",
    "reference"
    "https://ossindex.sonatype.org/component/pkg:maven/mysql/mysql-connector-java@8.0.16?utm_source=apache-httpclient&utm_medium=integration&utm_content=4.5.13",
    "vulnerabilities"
    [{"cvssVector" "CVSS:3.1/AV:N/AC:H/PR:N/UI:R/S:U/C:L/I:L/A:L",
      "cvssScore" 5.0,
      "displayName" "CVE-2020-2934",
      "id" "CVE-2020-2934",
      "reference"
      "https://ossindex.sonatype.org/vulnerability/CVE-2020-2934?component-type=maven&component-name=mysql%2Fmysql-connector-java&utm_source=apache-httpclient&utm_medium=integration&utm_content=4.5.13",
      "title" "[CVE-2020-2934] CWE-noinfo",
      "cve" "CVE-2020-2934",
      "externalReferences"
      ["http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2020-2934"
       "https://dev.mysql.com/doc/relnotes/connector-j/5.1/en/news-5-1-49.html"
       "https://dev.mysql.com/doc/relnotes/connector-j/8.0/en/news-8-0-20.html"
       "https://www.oracle.com/security-alerts/cpuapr2020.html"
       "https://www.codetd.com/en/article/8221726"],
      "description"
      "Vulnerability in the MySQL Connectors product of Oracle MySQL (component: Connector/J). Supported versions that are affected are 8.0.19 and prior and 5.1.48 and prior. Difficult to exploit vulnerability allows unauthenticated attacker with network access via multiple protocols to compromise MySQL Connectors. Successful attacks require human interaction from a person other than the attacker. Successful attacks of this vulnerability can result in unauthorized update, insert or delete access to some of MySQL Connectors accessible data as well as unauthorized read access to a subset of MySQL Connectors accessible data and unauthorized ability to cause a partial denial of service (partial DOS) of MySQL Connectors. CVSS 3.0 Base Score 5.0 (Confidentiality, Integrity and Availability impacts). CVSS Vector: (CVSS:3.0/AV:N/AC:H/PR:N/UI:R/S:U/C:L/I:L/A:L).",
      "cwe" "CWE-noinfo"}
     {"cvssVector" "CVSS:3.1/AV:N/AC:H/PR:H/UI:N/S:U/C:H/I:N/A:H",
      "cvssScore" 5.9,
      "displayName" "CVE-2021-2471",
      "id" "CVE-2021-2471",
      "reference"
      "https://ossindex.sonatype.org/vulnerability/CVE-2021-2471?component-type=maven&component-name=mysql%2Fmysql-connector-java&utm_source=apache-httpclient&utm_medium=integration&utm_content=4.5.13",
      "title" "[CVE-2021-2471] CWE-611: Improper Restriction of XML External Entity Reference ('XXE')",
      "cve" "CVE-2021-2471",
      "externalReferences"
      ["http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2021-2471"
       "https://www.oracle.com/security-alerts/cpuoct2021.html"],
      "description"
      "Vulnerability in the MySQL Connectors product of Oracle MySQL (component: Connector/J). Supported versions that are affected are 8.0.26 and prior. Difficult to exploit vulnerability allows high privileged attacker with network access via multiple protocols to compromise MySQL Connectors. Successful attacks of this vulnerability can result in unauthorized access to critical data or complete access to all MySQL Connectors accessible data and unauthorized ability to cause a hang or frequently repeatable crash (complete DOS) of MySQL Connectors. CVSS 3.1 Base Score 5.9 (Confidentiality and Availability impacts). CVSS Vector: (CVSS:3.1/AV:N/AC:H/PR:H/UI:N/S:U/C:H/I:N/A:H).",
      "cwe" "CWE-611"}
     {"cvssVector" "CVSS:3.1/AV:N/AC:H/PR:H/UI:N/S:U/C:H/I:H/A:H",
      "cvssScore" 6.6,
      "displayName" "CVE-2022-21363",
      "id" "CVE-2022-21363",
      "reference"
      "https://ossindex.sonatype.org/vulnerability/CVE-2022-21363?component-type=maven&component-name=mysql%2Fmysql-connector-java&utm_source=apache-httpclient&utm_medium=integration&utm_content=4.5.13",
      "title" "[CVE-2022-21363] CWE-310",
      "cve" "CVE-2022-21363",
      "externalReferences"
      ["http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2022-21363"
       "https://www.oracle.com/security-alerts/cpujan2022.html"],
      "description"
      "Vulnerability in the MySQL Connectors product of Oracle MySQL (component: Connector/J). Supported versions that are affected are 8.0.27 and prior. Difficult to exploit vulnerability allows high privileged attacker with network access via multiple protocols to compromise MySQL Connectors. Successful attacks of this vulnerability can result in takeover of MySQL Connectors. CVSS 3.1 Base Score 6.6 (Confidentiality, Integrity and Availability impacts). CVSS Vector: (CVSS:3.1/AV:N/AC:H/PR:H/UI:N/S:U/C:H/I:H/A:H).",
      "cwe" "CWE-310"}]}])

(defn- handler
  [_]
  {:status 200
   :body (json/write-str reports)})

(defn start!
  "Starts the sonatype mock, returning the port on which
   it was started as :port and a function to stop it as :stop"
  []
  (let [port 8284
        stop! (httpkit/run-server handler {:port port})]
    {:stop! stop!
     :port port}))
