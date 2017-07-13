package ufla.br.ast.genggets.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeVisitor extends ASTVisitor {
	
	List<TypeDeclaration> types = new ArrayList<TypeDeclaration>();

    @Override
    public boolean visit(TypeDeclaration node) {
    	types.add(node);
            return super.visit(node);
    }

    public List<TypeDeclaration> getTypes() {
            return types;
    }
}
