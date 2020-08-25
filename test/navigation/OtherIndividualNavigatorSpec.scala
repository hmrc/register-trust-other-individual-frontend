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

package navigation

import base.SpecBase
import config.FrontendAppConfig
import controllers.register.individual.{routes => irts}
import controllers.register.{routes => rts}
import generators.Generators
import models.{FullName, UserAnswers}
import models.register.pages.{AddOtherIndividual, IndividualOrBusinessToAdd}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register._
import pages.register.individual.NamePage
import play.api.mvc.Call

class OtherIndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private def otherIndividualsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  val navigator: OtherIndividualNavigator = injector.instanceOf[OtherIndividualNavigator]

   "AnswersPage" when {
     "go to AddOtherIndividualPage from AnswersPage" in {
       forAll(arbitrary[UserAnswers]) {
         userAnswers =>
           navigator.nextPage(AnswersPage, fakeDraftId, userAnswers)
             .mustBe(controllers.register.routes.AddOtherIndividualController.onPageLoad(fakeDraftId))
       }
     }
   }


  "AddOtherIndividualYesNoPage" when {

    "go to NamePage from AddOtherIndividualYesNoPage when selected yes" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val index = 0
          val answers = userAnswers.set(AddOtherIndividualYesNoPage, true).success.value

          navigator.nextPage(AddOtherIndividualYesNoPage, fakeDraftId, answers)
            .mustBe(controllers.register.individual.routes.NameController.onPageLoad(index, fakeDraftId))
      }

    }

    "go to RegistrationProgress from AddOtherIndividualYesNoPage when selected no" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddOtherIndividualYesNoPage, false).success.value

          navigator.nextPage(AddOtherIndividualYesNoPage, fakeDraftId, answers)
            .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

    "go to NamePage from AddOtherIndividualPage when selected add them now" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val index = 0
          val answers = userAnswers.set(AddOtherIndividualPage, AddOtherIndividual.YesNow).success.value

          navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
            .mustBe(controllers.register.individual.routes.NameController.onPageLoad(index, fakeDraftId))
      }

    }
  }


  "go to RegistrationProgress from AddOtherIndividualPage" when {

    "selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers
            .set(NamePage(0), FullName("First", None, "Last")).success.value
            .set(AddOtherIndividualPage, AddOtherIndividual.YesLater).success.value

          navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
            .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

    "selecting added them all" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers
            .set(NamePage(0), FullName("First", None, "Last")).success.value
            .set(AddOtherIndividualPage, AddOtherIndividual.NoComplete).success.value

          navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
            .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

  }

  "TrustHasOtherIndividualYesNoPage" when {

    "go to InfoPage from TrustHasOtherIndividualYesNoPage when yes selected" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(TrustHasOtherIndividualYesNoPage, value = true).success.value

          navigator.nextPage(TrustHasOtherIndividualYesNoPage, fakeDraftId, answers)
            .mustBe(rts.InfoController.onPageLoad(fakeDraftId))
      }
    }

    "go to RegistrationProgress from TrustHasOtherIndividualYesNoPage when no selected" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers.set(TrustHasOtherIndividualYesNoPage, value = false).success.value

          navigator.nextPage(TrustHasOtherIndividualYesNoPage, fakeDraftId, answers)
            .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
      }
    }

  }

}
