package ufla.br.ast.genggets.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class GenerateGetsHandler extends AbstractHandler {

        private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

        @Override
        public Object execute(ExecutionEvent event) 
        		throws ExecutionException {
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                IWorkspaceRoot root = workspace.getRoot();
                // Recupera todos projetos do workspace
                IProject[] projects = root.getProjects();
                // Itera sobre todos os projetos
                for (IProject project : projects) {
                        try {
                                if (project.isNatureEnabled(JDT_NATURE)) {
                                	createGetProject(project);
                                }
                        } catch (CoreException e) {
                                e.printStackTrace();
                        } catch (MalformedTreeException e) {
							e.printStackTrace();
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
                }
                return null;
        }
        
        private void createGetProject(IProject project) 
        		throws JavaModelException, MalformedTreeException, BadLocationException {
        	IPackageFragment[] packages = JavaCore.create(project)
                    .getPackageFragments();
        	for (IPackageFragment mypackage : packages) {
        		if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
        			createGetPackage(mypackage);
        		}

        	}
        }
        
        private void createGetPackage(IPackageFragment mypackage) 
        		throws JavaModelException, MalformedTreeException, BadLocationException {
        	for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
        		createGetsCompUnit(unit);
        	}
        }
        
        private void createGetsCompUnit(ICompilationUnit unit) 
        		throws JavaModelException, MalformedTreeException,
        		BadLocationException {
        	Document document= new Document(unit.getSource());
        	ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setSource(unit);
            CompilationUnit compUnit = (CompilationUnit) parser.createAST(null);
            // começa a registrar as modificações
            compUnit.recordModifications();
			AST ast = compUnit.getAST();
           
			TypeVisitor visitor = new TypeVisitor();
            compUnit.accept(visitor);
            for (TypeDeclaration type : visitor.getTypes()) {
            	createGetsType(type, ast);
            }
            
			// computação do texto editado
			TextEdit edits = compUnit.rewrite(document, 
					unit.getJavaProject().getOptions(true));
			
			// computação do novo código-fonte
			edits.apply(document);
			String newSource = document.get();
			
			// atualização da unidade de compilação
			unit.getBuffer().setContents(newSource);

        }
        
        private void createGetsType(TypeDeclaration type, AST ast) {
        	for (FieldDeclaration field : type.getFields()) {
        		Type varType = field.getType();
        		for (Object varObj : field.fragments()) {
        			MethodDeclaration getMethod = createGetVar(
        					(VariableDeclarationFragment) varObj, 
        					ast, varType);
        			type.bodyDeclarations().add(getMethod);
        		}
        			
        	}
        }
        
        /**
         * Cria um método get para uma variável
         * @param var variável
         * @param ast ast
         * @param varType tipo da variável
         * @return
         */
        private MethodDeclaration createGetVar(VariableDeclarationFragment var, 
        		AST ast, Type varType) {
        	// Declara um método público
        	MethodDeclaration getMethod = ast.newMethodDeclaration();
			getMethod.setConstructor(false);
			List<Modifier> modifiers = getMethod.modifiers();
			modifiers.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			
			//Define o nome do método
			String varNameStr = var.getName().getFullyQualifiedName();
			char firstLetter = Character.toUpperCase(varNameStr.charAt(0));
			String getNameStr = "get" + firstLetter + varNameStr.substring(1);
			SimpleName getName = ast.newSimpleName(getNameStr);
			getMethod.setName(getName);
			
			Block block = ast.newBlock();
			ReturnStatement ret = ast.newReturnStatement();
			Name varReturnExpres = ast.newName(varNameStr);
			ret.setExpression(varReturnExpres);
			block.statements().add(ret);
			
			getMethod.setBody(block);
			if (varType.isPrimitiveType()) {
				PrimitiveType pt = (PrimitiveType) varType;
				Type nt = ast.newPrimitiveType(pt.getPrimitiveTypeCode());
				getMethod.setReturnType2(nt);
			} else if (varType.isSimpleType()) {
				SimpleType st = (SimpleType) varType;
				Name name = ast.newName(st.getName().getFullyQualifiedName());
				Type nt = ast.newSimpleType(name);
				getMethod.setReturnType2(nt);
			}
			return getMethod;
        }
 
        
}
