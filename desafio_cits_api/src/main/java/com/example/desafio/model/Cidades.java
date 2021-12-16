package com.example.desafio.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="cidades")
@XmlAccessorType(XmlAccessType.FIELD)
public class Cidades {
	
	@XmlElement
	List<Cidade> cidade= new ArrayList<Cidade>();

	//Getters e setters
	public List<Cidade> getCidade() {
		return cidade;
	}

	public void setPessoa(List<Cidade> cidade) {
		this.cidade = cidade;
	}
}
