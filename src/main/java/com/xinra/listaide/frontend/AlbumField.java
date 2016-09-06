package com.xinra.listaide.frontend;

import com.xinra.listaide.service.AlbumDTO;

public class AlbumField extends LinkField<AlbumDTO> {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getLinkText(AlbumDTO value) {
		return value.getName();
	}

	@Override
	protected String getLinkUrl(AlbumDTO value) {
		return value.getUrl();
	}

	@Override
	public Class<? extends AlbumDTO> getType() {
		return AlbumDTO.class;
	}

}
