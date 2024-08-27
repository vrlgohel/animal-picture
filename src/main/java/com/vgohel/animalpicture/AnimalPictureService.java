package com.vgohel.animalpicture;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class AnimalPictureService {

    private final RestTemplate restTemplate = new RestTemplate();

    @JobWorker(type = "getAnimalType", autoComplete = true)
    public void handleImage(JobClient client, ActivatedJob job) {
        System.out.println("Handle Image Service Task Activated");

        String animal = (String) job.getVariablesAsMap().get("animalType");

        String getAnimal = getImageURL(animal);

        String response = restTemplate.getForObject(getAnimal, String.class);

        String imageURL = extractImage(response, animal);

        Map<String, Object> variables = new HashMap<>();
        variables.put("imageURL", imageURL);

        client.newCompleteCommand(job.getKey()).variables(variables).send().join();

    }

    private String extractImage(String response, String animal) {
        if ("dogs".equalsIgnoreCase(animal)) {
            return new JSONObject(response).getString("message");
        }
        return response;
    }

    private String getImageURL(String animal) {
        if ("cats".equalsIgnoreCase(animal)) {
            return "https://api.thecatapi.com/v1/images/search";
        } else if ("dogs".equalsIgnoreCase(animal)) {
            return "https://dog.ceo/api/breeds/image/random";
        } else if ("bears".equalsIgnoreCase(animal)) {
            return "https://placebear.com/200/300";
        }
        return animal;
    }
}
