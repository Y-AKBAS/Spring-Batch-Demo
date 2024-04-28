
This application showcases how to create ItemReader,ItemProcessor,ItemWriter,
Step and a Job etc. as well as how to run these jobs with a SimpleTaskAsyncExecutor.

We have a simple **users** table which has id,first_name,last_name,email,full_name columns.
We have 10.000 user entities in our table and the full_name column is initially being set to
null. The demo purpose of our application is to set this full_name column with the help of a 
job: See **runner** package.

