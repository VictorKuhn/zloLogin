package com.zlologin.zlologin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.*;

class ZloLoginApplicationTests {

	@Test
	void mainMethodTest() {
		// Simula os argumentos passados ao main
		String[] args = new String[]{};

		// Usa um mock para o SpringApplication
		try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
			mockedSpringApplication.when(() -> SpringApplication.run(ZloLoginApplication.class, args))
					.thenReturn(null);

			// Chama o método main para verificar a cobertura
			ZloLoginApplication.main(args);

			// Verifica se SpringApplication.run foi chamado
			mockedSpringApplication.verify(() -> SpringApplication.run(ZloLoginApplication.class, args));
		}
	}
}
