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
    private String uid;

    public productos(String uid, String nProducto, String mProducto, String tProducto, String pProducto, String cProducto){
        this.setUid(uid);
        this.setnProducto(nProducto);
        this.setmProducto(mProducto);
        this.settProducto(tProducto);
        this.setpProducto(pProducto);
        this.setcProducto(cProducto);

    }


    public productos(){

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", getUid());
        result.put("nProducto", getnProducto());
        result.put("mProducto", getmProducto());
        result.put("tProducto", gettProducto());
        result.put("pProducto", getpProducto());
        result.put("cProducto", getcProducto());

        return result;
    }

    public String getnProducto() {
        return nProducto;
    }

    public void setnProducto(String nProducto) {
        this.nProducto = nProducto;
    }


    public String getmProducto() {
        return mProducto;
    }

    public void setmProducto(String mProducto) {
        this.mProducto = mProducto;
    }

    public String getpProducto() {
        return pProducto;
    }

    public void setpProducto(String pProducto) {
        this.pProducto = pProducto;
    }

    public String getcProducto() {
        return cProducto;
    }

    public void setcProducto(String cProducto) {
        this.cProducto = cProducto;
    }

    public String gettProducto() {
        return tProducto;
    }

    public void settProducto(String tProducto) {
        this.tProducto = tProducto;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return   "Nombre Producto: '" + nProducto + '\'' +
                "\n, Marca: '" + mProducto + '\'' +
                "\n, Tipo de Producto: '" + tProducto + '\'' +
                "\n, Cantidad Disponible: '" + cProducto + '\'' +
                "\n, Precio por unidad: '" + pProducto+ '\'' +
                "\n, identificador: '" + uid;
    }
}
