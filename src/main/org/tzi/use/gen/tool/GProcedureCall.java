/*
 * This is source code of the Snapshot Generator, an extension for USE
 * to generate (valid) system states of UML models.
 * Copyright (C) 2001 Joern Bohling, University of Bremen
 *
 * About USE:
 *   USE - UML based specification environment
 *   Copyright (C) 1999,2000,2001 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */


package org.tzi.use.gen.tool;

import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.Evaluator;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.gen.assl.statics.GProcedure;
import org.tzi.use.util.StringUtil;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;



/**
 * Represents the call of a procedure.
 * @author  Joern Bohling
 */
public class GProcedureCall {
    private String fName;
    private List fParameter;  // Expressions    

    public GProcedureCall (String name, List params) {
        fName = name;
        fParameter = params;
    }

    private ArrayList parameterTypes() {
        Iterator it = fParameter.iterator();
        ArrayList types = new ArrayList();
        while (it.hasNext())
            types.add( ((Expression) it.next()).type() );
        return types;
    }

    public List signature() {
        List types = (ArrayList) parameterTypes().clone();
        types.add(0, fName);
        return types;
    }

    public String signatureString() {
        return "procedure " + fName + "("
            + StringUtil.fmtSeq(parameterTypes().iterator(), ",") + ")";
    }
    
    public GProcedure findMatching( List procedures ) {
        List signature = signature();
        Iterator it = procedures.iterator();
        while (it.hasNext() ) {
            GProcedure proc = (GProcedure) it.next();
            if (proc.signature().equals(signature) )
                return proc;
        }
        return null;
    }


    public List evaluateParams(MSystemState state) {
        List values = new ArrayList();
        Evaluator evaluator = new Evaluator();
        Iterator it = fParameter.iterator();
        while (it.hasNext() )
            values.add( evaluator.eval( (Expression) it.next(),
                                        state,
                                        state.system().topLevelBindings()) );
        return values;
    }
}