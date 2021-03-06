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
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.flex.FlexGrammar;
import org.sonar.flex.FlexKeyword;
import org.sonar.flex.checks.utils.Clazz;
import org.sonar.flex.checks.utils.Modifiers;
import org.sonar.flex.checks.utils.Variable;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.Set;

@Rule(
  key = "S1170",
  priority = Priority.MAJOR)
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
public class PublicConstNotStaticCheck extends SquidCheck<LexerlessGrammar> {

  @Override
  public void init() {
    subscribeTo(FlexGrammar.CLASS_DEF);
  }

  @Override
  public void visitNode(AstNode astNode) {
    for (AstNode directive : Clazz.getDirectives(astNode)) {
      if (Variable.isConst(directive)) {
        Set<AstNodeType> varModifiers = Modifiers.getModifiers(directive.getFirstChild(FlexGrammar.ATTRIBUTES));

        if (varModifiers.contains(FlexKeyword.PUBLIC) && !varModifiers.contains(FlexKeyword.STATIC)) {
          getContext().createLineViolation(this, "Make this const field \"{0}\" static too", directive,
            Variable.getName(directive.getFirstChild(FlexGrammar.ANNOTABLE_DIRECTIVE).getFirstChild()));
        }
      }
    }
  }
}
