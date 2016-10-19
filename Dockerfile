FROM sandtable/scipystack:latest

RUN apk add openjdk8-jre

ADD housingModel_ST.jar /opt/

ADD modelsrc/data/AgeMarginalPDFstatic.csv /opt/
ADD modelsrc/data/IncomeGivenAge.csv /opt/

WORKDIR /opt

CMD java -jar housingModel_ST.jar
