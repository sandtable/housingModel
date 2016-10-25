docker_build:
		docker build -t inet/housingmodel:0.1 .

docker_run:
		docker run -t inet/housingmodel:0.1

ping:
		sandplatform --hostname $(SAND_HOSTNAME) ping

run:
		sandplatform run model.yaml

run_docker:
		sandplatform run model_docker.yaml

upload:
		sandplatform --hostname $(SAND_HOSTNAME) upload model model.yaml

project:
		sandplatform --hostname $(SAND_HOSTNAME) create project project.yaml
