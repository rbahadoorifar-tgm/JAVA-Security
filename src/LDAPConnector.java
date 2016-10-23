import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

/**
 * Created by Ramin on 21.10.2016.
 */
public class LDAPConnector {
    // Erforderliche Daten des Servers, welche zum Verbinden notwendig sind
    final private String ldapHost = "ldap://192.168.0.19:389";

    final String ldapUsername = "cn=admin,dc=nodomain,dc=com";
    final String ldapPassword = "user";
    private DirContext ctx;

    final String ldapPublicKeyPath = "cn=group.service1,dc=nodomain,dc=com";

    public LDAPConnector() {
        Hashtable<String, Object> env = new Hashtable<String, Object>(11);

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapHost);

        // Authentifizierung
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);

        try {
            //Verbindung erstellen
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setzen eines Public Keys. Diese Methode sollte nur vom Service verwendet werden.
     * @param publicKey Neuer Public Key
     */
    public void setPublicKey (String publicKey) {
        ModificationItem[] mods = new ModificationItem[1];
        Attribute mod0 = new BasicAttribute("description", publicKey);
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
        try {
            ctx.modifyAttributes(ldapPublicKeyPath, mods);
        } catch(NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Anfordern eines Public Keys. Diese Methode kann von beiden Akteur verwendet werden, wird in diesem Beispiel jedoch nur vom Client verwendet.
     * @return Public Key des Services
     */
    public String getPublicKey () {
        try {
            Attributes attributes = ctx.getAttributes(ldapPublicKeyPath);
            Attribute attribute = attributes.get("description");
            String value = (String) attribute.get(0);
            return value;
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }




}
