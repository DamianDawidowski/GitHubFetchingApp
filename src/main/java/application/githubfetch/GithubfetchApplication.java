package application.githubfetch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class GithubfetchApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubfetchApplication.class, args);

		String url = "https://catfact.ninja/fact?max_length=155";

		WebClient.Builder  builder = WebClient.builder();

		String catFact = builder.build()
				.get().uri(url).retrieve().bodyToMono(String.class).block();

		System.out.println("------------------------------");
		System.out.println(catFact);
		System.out.println("------------------------------");
	}

}
