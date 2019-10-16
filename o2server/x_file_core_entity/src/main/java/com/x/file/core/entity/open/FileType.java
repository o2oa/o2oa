package com.x.file.core.entity.open;

import com.x.base.core.entity.JpaObject;

/**
 *
 */
public enum FileType {

	image, office, music, movie, other;

	public static final int length = JpaObject.length_64B;

	public static String getExtType( String ext ){
		String type = FileType.other.name();
		if( "jpg".equalsIgnoreCase( ext ) ){ type = FileType.image.name();
		} else if("jpeg".equalsIgnoreCase( ext ) ){ type = FileType.image.name();
		} else if("png".equalsIgnoreCase( ext ) ){ type = FileType.image.name();
		} else if("tif".equalsIgnoreCase( ext ) ){ type = FileType.image.name();
		} else if("bmp".equalsIgnoreCase( ext ) ){ type = FileType.image.name();
		} else if("gif".equalsIgnoreCase( ext ) ){ type = FileType.image.name();
		} else if("jpe".equalsIgnoreCase( ext ) ){ type = FileType.image.name();
		} else if("xls".equalsIgnoreCase( ext ) ){ type = FileType.office.name();
		} else if("xlsx".equalsIgnoreCase( ext ) ){ type = FileType.office.name();
		} else if("doc".equalsIgnoreCase( ext ) ){ type = FileType.office.name();
		} else if("docx".equalsIgnoreCase( ext ) ){ type = FileType.office.name();
		} else if("ppt".equalsIgnoreCase( ext ) ){ type = FileType.office.name();
		} else if("pptx".equalsIgnoreCase( ext ) ){ type = FileType.office.name();
		} else if("pdf".equalsIgnoreCase( ext ) ){ type = FileType.office.name();
		} else if("txt".equalsIgnoreCase( ext ) ){ type = FileType.office.name();
		} else if("mp3".equalsIgnoreCase( ext ) ){ type = FileType.music.name();
		} else if("mp4".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("wmv".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("rmvb".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("mkv".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("avi".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("3gp".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("flv".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("mov".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("mpv".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("mpeg".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("mpg".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("mts".equalsIgnoreCase( ext ) ){ type = FileType.movie.name();
		} else if("zip".equalsIgnoreCase( ext ) ){ type = FileType.other.name();
		} else if("rar".equalsIgnoreCase( ext ) ){ type = FileType.other.name();
		}

		return type;
	}
}
