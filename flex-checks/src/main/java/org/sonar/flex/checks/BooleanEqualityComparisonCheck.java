/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.flex.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.flex.FlexGrammar;
import org.sonar.flex.FlexKeyword;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(
  key = "S1125",
  priority = Priority.MINOR)
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MINOR)
public class BooleanEqualityComparisonCheck extends SquidCheck<LexerlessGrammar> {

  @Override
  public void init() {
    subscribeTo(FlexGrammar.EQUALITY_EXPR);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (hasBooleanLiteralOperand(astNode.getFirstChild(FlexGrammar.EQUALITY_OPERATOR).getNextAstNode())) {
      getContext().createLineViolation(this, "Remove the unnecessary boolean comparison to simplify this expression.", astNode);
    }
  }

  private static boolean hasBooleanLiteralOperand(AstNode astNode) {
    return astNode.is(FlexGrammar.POSTFIX_EXPR)
      && astNode.getFirstChild().is(FlexGrammar.PRIMARY_EXPR)
      && astNode.getFirstChild().getFirstChild().is(FlexKeyword.TRUE, FlexKeyword.FALSE);
  }
}
