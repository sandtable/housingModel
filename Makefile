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

fetch-config:
	mkdir -p platform_results/$(cid) && cd platform_results/$(cid) && sandplatform configurations data get --project_name housing_inet --configuration_id $(cid)

fetch-rslt:
	mkdir -p platform_results/calibrations/$(mcid) && cd platform_results/calibrations/$(mcid) && sandplatform calibrations results --project_name housing_inet --calibration_id $(mcid) --path parm.dat

fetch-clbr: fetch-rslt
	mkdir -p platform_results/calibrations/$(mcid) && cd platform_results/calibrations/$(mcid) && sandplatform calibrations data get --project_name housing_inet --calibration_id $(mcid)
