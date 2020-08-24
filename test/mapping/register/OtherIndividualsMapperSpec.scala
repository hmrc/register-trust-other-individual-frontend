/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mapping.register

import base.SpecBase
import generators.Generators
import mapping.Mapping
import models.FullName
import org.scalatest.{MustMatchers, OptionValues}
import pages.register._

class OtherIndividualsMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val otherIndividualsMapper : Mapping[OtherIndividualsType] = injector.instanceOf[OtherIndividualsMapper]

  "OtherIndividualsMapper" when {

    "when user answers is empty" must {

      "not be able to create OtherIndividualsType" in {

        val userAnswers = emptyUserAnswers

        otherIndividualsMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "be able to create OtherIndividualsType when there is an individual otherIndividual" in {

        val index = 0

        val userAnswers = emptyUserAnswers
          .set(individual.NamePage(index), FullName("first", None, "last")).success.value
          .set(individual.DateOfBirthYesNoPage(index), false).success.value
          .set(individual.NationalInsuranceYesNoPage(index), true).success.value
          .set(individual.NationalInsuranceNumberPage(index), "AB123456C").success.value

        val result = otherIndividualsMapper.build(userAnswers).value

        result.otherIndividual mustBe defined
      }
    }
  }
}
