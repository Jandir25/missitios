package com.tfc.misguias;

import java.util.Date;

public class SingletonDatosLista {
        private String name = null;
        private Date date = null;
        private long IdGuide=-1;
        private int ownList=-1; 
        private long idSelected = -1;
        private long idPlaceSelected = -1;

    public long getIdSelected() {
			return idSelected;
		}

	public void setIdSelected(long idSelectted) {
			this.idSelected = idSelectted;
		}

	public int getOwnList() {
			return ownList;
		}

	public void setOwnList(int ownList) {
			this.ownList = ownList;
		}

	public long getIdGuide() {
			return IdGuide;
		}

	public void setIdGuide(long idList) {
			this.IdGuide = idList;
		}

	// SINGLETON DEFINITION
    private static SingletonDatosLista INSTANCE = null;

    private SingletonDatosLista() {}
 
    private synchronized static void createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SingletonDatosLista();
        }
    }
 
    public static SingletonDatosLista getInstance() {
        if (INSTANCE == null) createInstance();
        return INSTANCE;
    }

    // GETTERS AND SETTERS 


    public String geName() {
        return this.name;
    }

    public void setNamet(String name) {
        this.name = name;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

	public long getIdPlaceSelected() {
		return idPlaceSelected;
	}

	public void setIdPlaceSelected(long idPlaceSelected) {
		this.idPlaceSelected = idPlaceSelected;
	}
}