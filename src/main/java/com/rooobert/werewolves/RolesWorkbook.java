package com.rooobert.werewolves;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RolesWorkbook {
	
	public static List<Role> load(Path ... workbooks) throws IOException, Exception {
		List<Role> roles = new ArrayList<>();
		
		// Load workbook
		for (Path path : workbooks) {
			try (FileInputStream fis = new FileInputStream(path.toFile());
					Workbook workbook = new XSSFWorkbook(fis)) {
				// Access sheet
				final Sheet sheetCards = workbook.getSheet("CARDS");
				
				// Collect all rows to process
				for (int rowIndex = 1; rowIndex <= sheetCards.getLastRowNum(); rowIndex++) {
					// Create node
					final Row row = sheetCards.getRow(rowIndex);
					
					// Read node data
					final String name = getStringCell(row, 0);
					final Set<String> teams = new HashSet<>(Arrays.asList(getStringCell(row, 1).split("\\s+,\\s+")));
					final int min = getIntegerCell(row, 2);
					final int max = getIntegerCell(row, 3);
					final String behaviour =  getStringCell(row, 4);
					final String description =  getStringCell(row, 5);
					
					// Load image
					Path imagePath = Paths.get("images", name + ".png");
					if (!Files.exists(imagePath)) {
						throw new RuntimeException("Image does not exist : " + imagePath.toString());
					}
					BufferedImage image = ImageIO.read(imagePath.toFile());
					
					// Save new node
					roles.add(new CustomRole(name, description, min, max, image, behaviour, teams));
				}
			}
		}
		
		return roles;
	}
	
	public static String getStringCell(Row row, int columnIndex) {
		final Cell cell = row.getCell(columnIndex);
		if (cell == null) {
			return null;
		}
		return cell.getStringCellValue().trim();
	}
	
	public static int getIntegerCell(Row row, int columnIndex) {
		final Cell cell = row.getCell(columnIndex);
		if (cell == null) {
			return 0;
		}
		return (int) Math.round(cell.getNumericCellValue());
	}
	
	public static boolean isNullOrBlank(String s) {
		return s == null || s.isBlank();
	}
}
