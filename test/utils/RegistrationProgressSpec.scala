/*
 * Copyright 2025 HM Revenue & Customs
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

package utils

import base.SpecBase
import models.Status.{Completed, InProgress}
import pages.register.TrustHasOtherIndividualYesNoPage
import play.api.libs.json.Json
import sections.OtherIndividuals
import viewmodels.addAnother.OtherIndividualViewModel
import utils.RegistrationProgress

class RegistrationProgressSpec extends SpecBase{
  implicit val writes = Json.writes[OtherIndividualViewModel]
  val registrationProgress = new RegistrationProgress()

  "RegistrationProgress" when {
    ".otherIndividualsStatus" should{
      "return in-progress when we added other individiuals" in {
        val userAnswers = emptyUserAnswers
        //.set(OtherIndividuals, List(OtherIndividualViewModel(None,InProgress))).success.value
        .set(TrustHasOtherIndividualYesNoPage,true).success.value

        val status = registrationProgress.otherIndividualsStatus(userAnswers)

        status.value mustBe InProgress
      }
      "return completed when we added other individiuals" in {
        val userAnswers = emptyUserAnswers
          //.set(OtherIndividuals, List(OtherIndividualViewModel(None,InProgress))).success.value
          .set(TrustHasOtherIndividualYesNoPage,false).success.value

        val status = registrationProgress.otherIndividualsStatus(userAnswers)

        status.value mustBe Completed
      }
      "return no status when trustHasOtherIndividuals has no answer" in {
        val userAnswers = emptyUserAnswers
          //.set(OtherIndividuals, List(OtherIndividualViewModel(None,InProgress))).success.value

        val status = registrationProgress.otherIndividualsStatus(userAnswers)

        status mustBe None
      }
    }
  }



}
