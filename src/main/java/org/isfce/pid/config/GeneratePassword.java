package org.isfce.pid.config;

import javax.swing.JOptionPane;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Petite fonction qui permet de connaître le PW codé pour l'insérer dans la BD
 * manuellement
 * @author Didier
 *
 */
public class GeneratePassword {
	public PasswordEncoder encoder() {
			 return new BCryptPasswordEncoder();
			 }
 public static void main(String[] args) {
	GeneratePassword o=new GeneratePassword();
	String pw=JOptionPane.showInputDialog("Encoder un PW");
	String pwCode=o.encoder().encode(pw);
	System.out.println("taille"+pwCode.length()+" code= "+pwCode);
	System.out.println("Valide : "+o.encoder().matches(pw, pwCode));
	}

}
