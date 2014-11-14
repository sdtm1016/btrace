/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.sun.btrace.runtime;

import com.sun.btrace.org.objectweb.asm.ClassReader;
import com.sun.btrace.org.objectweb.asm.ClassVisitor;
import com.sun.btrace.org.objectweb.asm.MethodVisitor;
import com.sun.btrace.org.objectweb.asm.Opcodes;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import support.InstrumentorTestBase;

/**
 *
 * @author Jaroslav Bachorik
 */
public class StackTrackingMethodVisitorTest extends InstrumentorTestBase {
    private ClassReader reader;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        byte[] data = loadTargetClass("StackTrackerTest");
        System.err.println(asmify(data));
        reader = new ClassReader(data);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void sanityTrackerTest() throws Exception {
        // just make sure that a sufficiently complex methods won't cause
        // any problems for tracking the stack
        reader.accept(new ClassVisitor(Opcodes.ASM5) {
            private String clzName;
            @Override
            public void visit(int i, int i1, String className, String string1, String string2, String[] strings) {
                this.clzName = className;
                super.visit(i, i1, className, string1, string2, strings);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, sig, exceptions);
                return new StackTrackingMethodVisitor(mv, clzName, desc, ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC));
            }

        }, ClassReader.EXPAND_FRAMES);
    }
}
