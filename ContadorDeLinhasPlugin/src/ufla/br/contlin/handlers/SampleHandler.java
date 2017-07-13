package ufla.br.contlin.handlers;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();
		
		IFile file = null;
		try {
			file = (IFile) workbenchPart.getSite().getPage()
				.getActiveEditor().getEditorInput().getAdapter(IFile.class);
		} catch(NullPointerException e) {
			MessageDialog.openInformation(window.getShell(),
					"CotadorDeLinhasPlugin", "Não existem arquivos abertos");
			return null;
		}
		if (file == null) {
			MessageDialog.openInformation(window.getShell(),
					"CotadorDeLinhasPlugin", "Não existem arquivos abertos");
			return null;
		}
		

		try {
			int i, contador = 1;
			InputStream in = file.getContents();

			while ((i = in.read()) != -1) {
				if (i == '\n') {
					contador++;
				}
			}
			in.close();
			if(contador == 1) {
				MessageDialog.openInformation(window.getShell(), 
						"CotadorDeLinhasPlugin", "Existe 1 linha em " + 
				file.getName() + "!");
			} else {
				MessageDialog.openInformation(window.getShell(),
						"CotadorDeLinhasPlugin", "Existem " + contador + 
						" linhas em " + file.getName() + "!");
			}

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
