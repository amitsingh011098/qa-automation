Prerequisite: Install Testng before running the testcases
As I was not able to install tesnt from marketplace so i did below :
Open Eclipse

Go to: Help → Eclipse Marketplace

Search for TestNG → Click Install

Restart Eclipse when prompted


Ensure the following are installed and properly configured:

Java JDK 17+

Apache Maven

Google Chrome

Eclipse IDE or IntelliJ IDEA




/qa_automation/src/test/java/com/automation/testcases/
├── TestCase1SearchVerification.java       // Search and product details
├── TestCase2CartValidation.java           // Cart functionality
├── TestCase3AppleFormValidation.java      // Apple ID form validation

Right-click on the file → Run As → TestNG Test



Output Files
Screenshots → saved in /screenshots/ folder

Result logs → saved in testResults.txt
