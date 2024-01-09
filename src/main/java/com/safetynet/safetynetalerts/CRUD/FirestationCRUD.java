package com.safetynet.safetynetalerts.CRUD;

import java.io.IOException;
import java.util.List;

import com.safetynet.safetynetalerts.model.Firestation;

public interface FirestationCRUD {

	Firestation save(Firestation firestation) throws IOException;

	Firestation findByAddress(String address);

	List<Firestation> findAllByStation(int station);

	void delete(Firestation firestation) throws IOException;
}