Following are the steps to install and configure reveal-angularjs UI in local for development:

Install Git for windows. It can be downloaded from https://git-scm.com/download/win. While installing choose "Use Git from the windows Command Prompt". 


Check git installation by running **"git --version"** command from windows command line.


Install node.js. It can be downloaded from https://nodejs.org/en/download/. After install run **"node --version"** from windows command line to check the installation.


Clone the project to your local machine from GitHub 

Open windows command prompt and navigate to the project folder. 

Run the following command

**npm install -g grunt-cli bower**

Once it is installed successfully, run the following command

**npm install**

If everything installed properly, run the following command. This will run the build process and open the application in browser. Port 81 will be used so make sure it is available.

**grunt**

Keep the command prompt open. The grunt will watch the project folder for any file change. If there is a change, grunt will process it automatically and the change will reflect in the browser. (No need to refresh browser for every change)

To deploy application to production, run **"grunt prod"** and deploy files from builds/prod to production server.