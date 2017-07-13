package ufla.br.jdt.worksinfo.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class WorkspaceInfoHandler extends AbstractHandler {

	private WorkspaceInfo workspaceInfo;
	
	/**
	 * The constructor.
	 */
	public WorkspaceInfoHandler() {
		
	}
	
	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		try {
			setWorkspaceInfo();
		} catch (Exception e) {
			MessageDialog.openInformation(
					window.getShell(),
					"ExemploEclipseJDTPlugin",
					"Erro! Não foi possível executar o plug-in!");
			e.printStackTrace();
		}
		MessageDialog.openInformation(
				window.getShell(),
				"ExemploEclipseJDTPlugin",
				workspaceInfo.toString());
		return null;
	}
	
	/**
	 * Recupera as informações do workspace.
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private void setWorkspaceInfo() throws JavaModelException, CoreException {
		workspaceInfo = new WorkspaceInfo();
		// Recupera a raíz do workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
        // Recupera todos projetos do workspace
        IProject[] projects = root.getProjects();
        // Itera sobre todos os projetos
		for (IProject project : projects) {
			if (project.isOpen() && 
					project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
		        IJavaProject javaProject = JavaCore.create(project);
		        workspaceInfo.incrementProjects(1);
		    	setWorkspaceInfoPackages(javaProject);
			}
		}
        
	}
	
	/**
	 * Conta os pacotes de um determinado projeto Java.
	 * @param javaProject projeto Java a ser contado os pacotes.
	 * @throws JavaModelException
	 */
	private void setWorkspaceInfoPackages(IJavaProject javaProject) 
			throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				int numberOfFiles = workspaceInfo.getNumberOfFiles();
				setWorkspaceInfoFiles(mypackage);
				// incrementa pacotes somente se o pacote possui algum arquivo
				if (numberOfFiles < workspaceInfo.getNumberOfFiles()) {
					workspaceInfo.incrementPackages(1);
				}
			}
		}
	}
	
	/**
	 * Conta os arquivos de um determinado pacote do workspace.
	 * @param mypackage pacote que contará os arquivos
	 * @throws JavaModelException
	 */
	private void setWorkspaceInfoFiles(IPackageFragment mypackage) 
			throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			workspaceInfo.incrementFiles(1);
			Document doc = new Document(unit.getSource());
			workspaceInfo.incrementLines(doc.getNumberOfLines());
			setWorkspaceInfoMethods(unit);
		}
	}
	
	/**
	 * Conta os métodos de uma unidade de compilação.
	 * @param unit unidade de compilação a contar métodos
	 * @throws JavaModelException
	 */
	private void setWorkspaceInfoMethods(ICompilationUnit unit) 
			throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			workspaceInfo.incrementMethods(type.getMethods().length);
		}
	}
	
}
