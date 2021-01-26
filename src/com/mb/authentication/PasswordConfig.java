/**
 * @author Anand
 *
 */
package com.mb.authentication;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.mb.decryption.core.DecryptionCore;

public class PasswordConfig  implements PasswordEncoder{

	@Override
	public String encode(CharSequence rawPassword) {
	
		return rawPassword.toString();
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		DecryptionCore core = new DecryptionCore();
		String hashed = core.decrypt(rawPassword.toString(), "TOKO");
		
		if(core.decrypt(encodedPassword.toString(), "TOKO").equals(hashed)) {
			return true;
		}
		return false;
	}

}
