// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.trees.expressions;

import org.apache.doris.analysis.ArithmeticExpr.Operator;
import org.apache.doris.nereids.exceptions.UnboundException;
import org.apache.doris.nereids.trees.expressions.visitor.ExpressionVisitor;
import org.apache.doris.nereids.types.DataType;
import org.apache.doris.nereids.types.DecimalV2Type;
import org.apache.doris.nereids.types.DoubleType;
import org.apache.doris.nereids.types.coercion.AbstractDataType;
import org.apache.doris.nereids.types.coercion.NumericType;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Divide Expression.
 */
public class Divide extends BinaryArithmetic {

    public Divide(Expression left, Expression right) {
        super(left, right, Operator.DIVIDE);
    }

    @Override
    public Expression withChildren(List<Expression> children) {
        Preconditions.checkArgument(children.size() == 2);
        return new Divide(children.get(0), children.get(1));
    }

    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visitDivide(this, context);
    }

    @Override
    public AbstractDataType inputType() {
        return NumericType.INSTANCE;
    }

    @Override
    public DataType getDataType() throws UnboundException {
        DataType commonType = super.getDataType();
        if (commonType.isDecimalV2Type()) {
            return DecimalV2Type.SYSTEM_DEFAULT;
        } else {
            return DoubleType.INSTANCE;
        }
    }

    // Divide is implemented as a scalar function which return type is always nullable.
    @Override
    public boolean nullable() throws UnboundException {
        return true;
    }
}
