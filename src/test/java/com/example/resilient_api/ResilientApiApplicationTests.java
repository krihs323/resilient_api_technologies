package com.example.resilient_api;

import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import com.example.resilient_api.domain.usecase.TechnologyUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = ResilientApiApplication.class)
class ResilientApiApplicationTests {

	@MockBean
	private TechnologyPersistencePort technologyPersistencePort;

	@Autowired
	private TechnologyUseCase technologyUseCase;

//	@Test
//	void contextLoads() {
//	}

}
