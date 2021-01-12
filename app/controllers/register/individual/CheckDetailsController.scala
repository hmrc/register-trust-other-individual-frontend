/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.register.individual

import config.FrontendAppConfig
import config.annotations.OtherIndividual
import controllers.actions._
import controllers.actions.register.individual.NameRequiredAction
import javax.inject.Inject
import models.Status.Completed
import navigation.Navigator
import pages.entitystatus.OtherIndividualStatus
import pages.register.individual.CheckDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.print.OtherIndividualPrintHelper
import viewmodels.AnswerSection
import views.html.register.individual.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        repository: RegistrationsRepository,
                                        @OtherIndividual navigator: Navigator,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        val appConfig: FrontendAppConfig,
                                        printHelper: OtherIndividualPrintHelper,
                                        nameAction: NameRequiredAction
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
    implicit request =>

      val section: AnswerSection = printHelper.checkDetailsSection(request.userAnswers, request.otherIndividualName, index, draftId)
      Ok(view(section, index, draftId))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).async {
    implicit request =>

      val answers = request.userAnswers.set(OtherIndividualStatus(index), Completed)

      for {
        updatedAnswers <- Future.fromTry(answers)
        _ <- repository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(CheckDetailsPage, draftId, request.userAnswers))
  }
}
