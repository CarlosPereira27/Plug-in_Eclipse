package ufla.br.jdt.worksinfo.handlers;

public class WorkspaceInfo {
	
	private int numberOfProjects;
	private int numberOfPackages;
	private int numberOfFiles;
	private int numberOfMethods;
	private int numberOfLines;
	
	public WorkspaceInfo() {
		this(0, 0, 0, 0, 0);
	}
	
	public WorkspaceInfo(int numberOfProjects, int numberOfPackages, 
			int numberOfFiles, int numberOfMethods, int numberOfLines) {
		super();
		this.numberOfProjects = numberOfProjects;
		this.numberOfPackages = numberOfPackages;
		this.numberOfFiles = numberOfFiles;
		this.numberOfMethods = numberOfMethods;
		this.numberOfLines = numberOfLines;
	}


	public int getNumberOfProjects() {
		return numberOfProjects;
	}
	public void setNumberOfProjects(int numberOfProjects) {
		this.numberOfProjects = numberOfProjects;
	}
	public int getNumberOfPackages() {
		return numberOfPackages;
	}
	public void setNumberOfPackages(int numberOfPackages) {
		this.numberOfPackages = numberOfPackages;
	}
	public int getNumberOfFiles() {
		return numberOfFiles;
	}
	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}
	public int getNumberOfMethods() {
		return numberOfMethods;
	}
	public void setNumberOfMethods(int numberOfMethods) {
		this.numberOfMethods = numberOfMethods;
	}
	public void incrementProjects(int number) {
		numberOfProjects += number;
	}
	public void incrementPackages(int number) {
		numberOfPackages += number;
	}
	public void incrementFiles(int number) {
		numberOfFiles += number;
	}
	public void incrementMethods(int number) {
		numberOfMethods += number;
	}
	public void incrementLines(int number) {
		numberOfLines += number;
	}
	
	public String toString() {
		return "Informações do workspace\n" + numberOfProjects + " projetos\n" +
		numberOfPackages + " pacotes\n" + numberOfFiles + " arquivos\n" + 
		numberOfMethods + " métodos\n" + numberOfLines + " linhas";
	}
	

}
