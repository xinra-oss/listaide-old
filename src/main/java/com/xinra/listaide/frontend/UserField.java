package com.xinra.listaide.frontend;

import com.xinra.listaide.service.UserDTO;

public class UserField extends LinkField<UserDTO> {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getLinkText(UserDTO value) {
		return value.getId();
	}

	@Override
	protected String getLinkUrl(UserDTO value) {
		return value.getUrl();
	}

	@Override
	public Class<? extends UserDTO> getType() {
		return UserDTO.class;
	}
	
}