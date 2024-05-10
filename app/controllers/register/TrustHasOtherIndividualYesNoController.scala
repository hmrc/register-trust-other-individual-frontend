/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.register

import config.annotations.OtherIndividual
import controllers.actions.StandardActionSets
import forms.YesNoFormProvider
import models.TaskStatus
import navigation.Navigator
import pages.register.TrustHasOtherIndividualYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.TrustsStoreService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.TrustHasOtherIndividualYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustHasOtherIndividualYesNoController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        repository: RegistrationsRepository,
                                                        @OtherIndividual navigator: Navigator,
                                                        standardActionSets: StandardActionSets,
                                                        formProvider: YesNoFormProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: TrustHasOtherIndividualYesNoView,
                                                        trustsStoreService: TrustsStoreService
                                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = formProvider.withPrefix("trustHasOtherIndividualYesNo")

  def onPageLoad(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TrustHasOtherIndividualYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId))
  }

  def onSubmit(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrustHasOtherIndividualYesNoPage, value))
            _              <- repository.set(updatedAnswers)
            taskStatus     = if (value) TaskStatus.InProgress else TaskStatus.Completed
            _              <- trustsStoreService.updateTaskStatus(draftId, taskStatus)
          } yield Redirect(navigator.nextPage(TrustHasOtherIndividualYesNoPage, draftId, updatedAnswers))
      )
  }

}
