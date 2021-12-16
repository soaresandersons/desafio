package com.example.desafio.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Previsao {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String dia;
	@Column
	private String maxima;
	@Column
	private String minima;
	@Column
	private String tempo;
	@Column
	private String unidadeDeMedida = "C";//Valores: C = Celsius, K = Kelvin e F = Fahrenheit
	
	
	public Date dataObjeto() {
		Date dataConvertida = null;
		try {
			dataConvertida = new SimpleDateFormat("dd/MM/yyyy").parse(dia);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataConvertida;
	}
	
	public void alterarValores() {
		if(this.unidadeDeMedida.equals("K")) {
			Double novaMaxima = Integer.parseInt(this.maxima)+273.15;
			Double novaMinima = Integer.parseInt(this.minima)+273.15;
			this.maxima = novaMaxima.toString();
			this.minima = novaMinima.toString();
		}else if(this.unidadeDeMedida.equals("F")) {
			Double novaMaxima = (1.8 * Double.parseDouble(this.maxima))+32;
			Double novaMinima = (1.8 * Double.parseDouble(this.minima))+32;
			this.maxima = novaMaxima.toString();
			this.minima = novaMinima.toString();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getMaxima() {
		return maxima;
	}

	public void setMaxima(String maxima) {
		this.maxima = maxima;
	}

	public String getMinima() {
		return minima;
	}

	public void setMinima(String minima) {
		this.minima = minima;
	}

	public String getTempo() {
		return tempo;
	}

	public void setTempo(String tempo) {
		this.tempo = tempo;
	}

	public String getUnidadeDeMedida() {
		return unidadeDeMedida;
	}

	public void setUnidadeDeMedida(String unidadeDeMedida) {
		this.unidadeDeMedida = unidadeDeMedida;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dia == null) ? 0 : dia.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((maxima == null) ? 0 : maxima.hashCode());
		result = prime * result + ((minima == null) ? 0 : minima.hashCode());
		result = prime * result + ((tempo == null) ? 0 : tempo.hashCode());
		result = prime * result + ((unidadeDeMedida == null) ? 0 : unidadeDeMedida.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Previsao other = (Previsao) obj;
		if (dia == null) {
			if (other.dia != null)
				return false;
		} else if (!dia.equals(other.dia))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (maxima == null) {
			if (other.maxima != null)
				return false;
		} else if (!maxima.equals(other.maxima))
			return false;
		if (minima == null) {
			if (other.minima != null)
				return false;
		} else if (!minima.equals(other.minima))
			return false;
		if (tempo == null) {
			if (other.tempo != null)
				return false;
		} else if (!tempo.equals(other.tempo))
			return false;
		if (unidadeDeMedida == null) {
			if (other.unidadeDeMedida != null)
				return false;
		} else if (!unidadeDeMedida.equals(other.unidadeDeMedida))
			return false;
		return true;
	}

	
	
}
