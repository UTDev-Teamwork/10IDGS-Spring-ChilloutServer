package com.utdev.chilloutserver.service.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.utdev.chilloutserver.model.Usuario;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

@Service
public class JWTService {

    private KeyStore store = null;
    private FileInputStream in_cert = null;
    public  Key key = null;
    public  Certificate cert = null;
    public  KeyPair kp;
    public JWTService() {}

    public String getToken(Usuario user){
        try{
            File p12 = new File(getClass().getClassLoader().getResource("keystore.p12").getFile());
            in_cert = new FileInputStream(p12);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            store = KeyStore.getInstance("PKCS12");
            store.load(in_cert, "M4st3r.K3y".toCharArray());

            Enumeration aliasEnum = store.aliases();
            while(aliasEnum.hasMoreElements()){
                String keyName = (String)aliasEnum.nextElement();
                key = store.getKey(keyName,"M4st3r.K3y".toCharArray());
                cert = store.getCertificate(keyName);
            }

            kp = new KeyPair(cert.getPublicKey(),(PrivateKey)key);

            ArrayList authorities = new ArrayList();
            authorities.add(user.getRole());

            JWSSigner signer = new RSASSASigner(kp.getPrivate());
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .claim("authorities", authorities)
                    .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                    .build();
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("0001").build(),
                    claimsSet);

            signedJWT.sign(signer);
            String s = signedJWT.serialize();

            System.out.println(s);
            return s;

            // Esto se supone que es del lado del cliente (O ya cuando vaya a validar)
            // signedJWT = SignedJWT.parse(s);
            // JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) kp.getPublic());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

}
