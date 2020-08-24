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

import config.FrontendAppConfig
import controllers.register.individual.{routes => irts}
import controllers.register.{routes => rts}
import javax.inject.Inject
import models.ReadableUserAnswers
import models.register.pages.AddOtherIndividual
import models.register.pages.IndividualOrBusinessToAdd.{Business, Individual}
import pages.Page
import pages.register._
import play.api.mvc.Call
import sections.IndividualOtherIndividuals

class OtherIndividualNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = route(draftId, config)(page)(userAnswers)

  private def route(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AnswersPage => _ => rts.AddOtherIndividualController.onPageLoad(draftId)
    case AddOtherIndividualPage => addOtherIndividualRoute(draftId, config)
    case AddOtherIndividualYesNoPage => addOtherIndividualYesNoRoute(draftId, config)
    case IndividualOrBusinessPage => individualOrBusinessRoute(draftId)
    case TrustHasOtherIndividualYesNoPage => trustHasOtherIndividualRoute(draftId)
  }

  private def otherIndividualsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def trustHasOtherIndividualRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call =
    userAnswers.get(TrustHasOtherIndividualYesNoPage) match {
      case Some(true) => controllers.register.routes.InfoController.onPageLoad(draftId)
      case Some(false) => otherIndividualsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def individualOrBusinessRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call =
    userAnswers.get(IndividualOrBusinessPage) match {
      case Some(Individual) => routeToIndividualOtherIndividualIndex(userAnswers, draftId)
      case Some(Business) => routeToIndividualOtherIndividualIndex(userAnswers, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def routeToIndividualOtherIndividualIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val individualOtherIndividuals = userAnswers.get(IndividualOtherIndividuals).getOrElse(List.empty)
    irts.NameController.onPageLoad(individualOtherIndividuals.size, draftId)
  }

  private def addOtherIndividualRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    val addAnother = answers.get(AddOtherIndividualPage)
    addAnother match {
      case Some(AddOtherIndividual.YesNow) =>
        controllers.register.routes.IndividualOrBusinessController.onPageLoad(draftId)
      case Some(AddOtherIndividual.YesLater) => otherIndividualsCompletedRoute(draftId, config)
      case Some(AddOtherIndividual.NoComplete) => otherIndividualsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addOtherIndividualYesNoRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    val add = answers.get(AddOtherIndividualYesNoPage)

    add match {
      case Some(true) =>
        controllers.register.routes.IndividualOrBusinessController.onPageLoad(draftId)
      case Some(false) => otherIndividualsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

}

