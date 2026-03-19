package com.x.pan.core.entity;

import com.x.base.core.entity.JpaObject;

/**
 *
 */
public enum FileTypeEnum {

	image, office, music, movie, other;

	public static final int length = JpaObject.length_64B;

	public static String getExtType( String ext ){
		String type = FileTypeEnum.other.name();
		if( "jpg".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.image.name();
		} else if("jpeg".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.image.name();
		} else if("png".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.image.name();
		} else if("tif".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.image.name();
		} else if("bmp".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.image.name();
		} else if("gif".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.image.name();
		} else if("jpe".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.image.name();
		} else if("xls".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.office.name();
		} else if("xlsx".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.office.name();
		} else if("doc".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.office.name();
		} else if("docx".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.office.name();
		} else if("ppt".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.office.name();
		} else if("pptx".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.office.name();
		} else if("pdf".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.office.name();
		} else if("txt".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.office.name();
		} else if("mp3".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.music.name();
		} else if("flac".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.music.name();
		} else if("ape".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.music.name();
		} else if("wav".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.music.name();
		} else if("mp4".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("wmv".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("rmvb".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("mkv".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("avi".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("3gp".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("flv".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("mov".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("mpv".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("mpeg".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("mpg".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("mts".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.movie.name();
		} else if("zip".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.other.name();
		} else if("rar".equalsIgnoreCase( ext ) ){ type = FileTypeEnum.other.name();
		}

		return type;
	}

	public static boolean isOfficeFile(String ext){
		boolean flag = false;
		if("xlsx".equalsIgnoreCase( ext ) ){ flag = true;
		} else if("docx".equalsIgnoreCase( ext ) ){ flag = true;
		} else if("pptx".equalsIgnoreCase( ext ) ){ flag = true;
		}
		return flag;
	}
}
