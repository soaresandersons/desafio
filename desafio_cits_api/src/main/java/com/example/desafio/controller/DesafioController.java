package com.example.desafio.controller;

import java.io.Reader;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.desafio.model.Cidades;
import com.example.desafio.model.Previsao;
import com.example.desafio.model.Resposta;
import com.example.desafio.repository.RespostaRepository;

@RestController
@RequestMapping("/desafio")
public class DesafioController {
	
	@Autowired
	private RespostaRepository respostaRepository;

	/**
	 * Retorna todas as cidades que foram consultadas e salvas no banco
	 * **/
	@GetMapping
	public List<Resposta> listar(){
		return respostaRepository.findAll();
		
	}
	
	/**
	 * Retorna a previsão do tempo para uma cidade da qual o nome da cidade é informado através do parâmetro cidade
	 * Caso o serviço do CPTE/INPE retorne mais de uma cidade com o mesmo nome ou nenhuma o serviço não será executado
	 * Apenas executa se o serviço do CPTE/INPE retornar apenas uma cidade com o nome informado através do parâetro.
	 * 
	 * O parâmetro unidade se refere à unidade de medida da temperatura (C = Celsius, K = Kelvin e F = Fahrenheit)
	 * **/
	@GetMapping("/previsao/{cidade}/{unidade}")
	public ResponseEntity<Resposta> buscarTempoCidade(@PathVariable("cidade") String cidade, 
				@PathVariable("unidade") String unidade) throws JAXBException {
		Resposta resposta = new Resposta();
		Cidades lista = buscarCidade(cidade);
		if(lista.getCidade() != null) {
			if(lista.getCidade().size() ==  0) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}else if(lista.getCidade().size() ==  1) {
				Resposta dadoDB = respostaRepository.buscarPorId(lista.getCidade().get(0).getId());
				if(dadoDB == null || dadoDB.getId()==null) {
					//fazer requisição externa
					resposta = buscarPrevisaoCidade(lista.getCidade().get(0).getId());
					resposta = respostaRepository.save(resposta);
					for(Previsao p : resposta.getPrevisao()) {
						p.setUnidadeDeMedida(unidade.toUpperCase());
						p.alterarValores();
					}
					return new ResponseEntity<>(resposta, HttpStatus.OK);
				}else {
					Calendar horaTeste = Calendar.getInstance();
					horaTeste.setTime(new Date());
					horaTeste.add(Calendar.HOUR, -1); 
					if(dadoDB.getDataHoraAtualizacao().compareTo(horaTeste.getTime()) < 0) {
						//fazer requisição externa
						resposta = buscarPrevisaoCidade(lista.getCidade().get(0).getId());
						resposta = respostaRepository.save(resposta);
						for(Previsao p : resposta.getPrevisao()) {
							p.setUnidadeDeMedida(unidade.toUpperCase());
							p.alterarValores();
						}
						return new ResponseEntity<>(resposta, HttpStatus.OK);
					}else {
						for(Previsao p : dadoDB.getPrevisao()) {
							p.setUnidadeDeMedida(unidade.toUpperCase());
							p.alterarValores();
						}
						return new ResponseEntity<>(dadoDB, HttpStatus.OK);
					}
				}
			}else if(lista.getCidade().size() >  1) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * Retorna a previsão do tempo para uma cidade da qual o id é informado através do parâmetro id
	 * O parâmetro unidade se refere à unidade de medida da temperatura (C = Celsius, K = Kelvin e F = Fahrenheit)
	 * **/
	@GetMapping("/previsao/cidade/{id}/{unidade}")
	public ResponseEntity<Resposta> buscarTempoCidade(@PathVariable("id") Long id, @PathVariable("unidade") String unidade) throws JAXBException {
		Resposta resposta = new Resposta();
		Resposta dadoDB = respostaRepository.buscarPorId(id);
		if(dadoDB == null || dadoDB.getId()==null) {
			//fazer requisição externa
			resposta = buscarPrevisaoCidade(id);
			resposta = respostaRepository.save(resposta);
			for(Previsao p : resposta.getPrevisao()) {
				p.setUnidadeDeMedida(unidade.toUpperCase());
				p.alterarValores();
			}
			return new ResponseEntity<>(resposta, HttpStatus.OK);
		}else {
			Calendar horaTeste = Calendar.getInstance();
			horaTeste.setTime(new Date());
			horaTeste.add(Calendar.HOUR, -1); 
			if(dadoDB.getDataHoraAtualizacao().compareTo(horaTeste.getTime()) < 0) {
				//fazer requisição externa
				resposta = buscarPrevisaoCidade(id);
				resposta = respostaRepository.save(resposta);
				for(Previsao p : resposta.getPrevisao()) {
					p.setUnidadeDeMedida(unidade.toUpperCase());
					p.alterarValores();
				}
				return new ResponseEntity<>(resposta, HttpStatus.OK);
			}else {
				for(Previsao p : dadoDB.getPrevisao()) {
					p.setUnidadeDeMedida(unidade.toUpperCase());
					p.alterarValores();
				}
				return new ResponseEntity<>(dadoDB, HttpStatus.OK);
			}
		}
	}
	
	/**
	 * Retorna uma lista de cidades com Nome, UF e ID.
	 * **/
	@GetMapping("/cidades/{nome}")
	public ResponseEntity<Cidades> buscarCidades(@PathVariable("nome") String nome) throws JAXBException {
		RestTemplate restTemplate = new RestTemplate();
		String xmlData = restTemplate.getForObject("http://servicos.cptec.inpe.br/XML/listaCidades?city="+nome, String.class);
		Reader reader = new StringReader(xmlData);
		JAXBContext jaxbContext = JAXBContext.newInstance(Cidades.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Cidades cidades = (Cidades)unmarshaller.unmarshal(reader);
		return new ResponseEntity<>(cidades, HttpStatus.OK); 
	}
	
	/**
	 * Faz uma chamada ao serviço externo do CPTE/INPE para pesquisar uma cidade.
	 * **/
	public Cidades buscarCidade(String cidade) throws JAXBException{
		RestTemplate restTemplate = new RestTemplate();
		String xmlData = restTemplate.getForObject("http://servicos.cptec.inpe.br/XML/listaCidades?city="+cidade, String.class);
		Reader reader = new StringReader(xmlData);
		JAXBContext jaxbContext = JAXBContext.newInstance(Cidades.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Cidades cidades = (Cidades)unmarshaller.unmarshal(reader);
		return cidades;
	}
	
	/**
	 * Faz uma chamada ao serviço externo do CPTE/INPE para retornar a previsão do tempo de uma cidade informando seu id.
	 * **/
	public Resposta buscarPrevisaoCidade(Long id) throws JAXBException{
		RestTemplate restTemplate = new RestTemplate();
		String xmlData = restTemplate.getForObject("http://servicos.cptec.inpe.br/XML/cidade/"+id+"/previsao.xml", String.class);
		Reader reader = new StringReader(xmlData);
		JAXBContext jaxbContext = JAXBContext.newInstance(Resposta.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Resposta resposta = (Resposta)unmarshaller.unmarshal(reader);
		resposta.setId(id);
		resposta.setDataHoraAtualizacao(new Date());
		return resposta;
	}
}
