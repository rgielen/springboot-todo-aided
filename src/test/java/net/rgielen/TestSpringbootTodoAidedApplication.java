package net.rgielen;

import org.springframework.boot.SpringApplication;

public class TestSpringbootTodoAidedApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringbootTodoAidedApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
