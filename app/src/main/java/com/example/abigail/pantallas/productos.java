package com.example.abigail.pantallas;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class productos {

    private String nProducto;
    private String mProducto;
    private String pProducto;
    private String cProducto;
    private String tProducto;
    private String pId;

    public productos(String pId, String nProducto, String mProducto, String tProducto, String pProducto, String cProducto){
        this.pId = pId;
        this.nProducto =  nProducto;
        this.mProducto = mProducto;
        this.tProducto = tProducto;
        this.pProducto = pProducto;
        this.cProducto = cProducto;

    }


    public productos(){

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("pId", pId);
        result.put("nombre", nProducto);
        result.put("marca", mProducto);
        result.put("tipo", tProducto);
        result.put("precio", pProducto);
        result.put("cantidad", cProducto);

        return result;
    }

    public String getnProducto() {
        return nProducto;
    }

    public void setnProducto(String nProducto) {
        this.nProducto = nProducto;
    }


}
