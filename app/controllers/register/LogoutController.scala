/*
 * Copyright 2023 HM Revenue & Customs
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

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import controllers.actions.register.RegistrationIdentifierAction
import play.api.Logging
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.Session

import scala.concurrent.ExecutionContext

@Singleton
class LogoutController @Inject()(appConfig: FrontendAppConfig,
                                 val controllerComponents: MessagesControllerComponents,
                                 identify: RegistrationIdentifierAction,
                                 auditConnector: AuditConnector
                                )(implicit val ec: ExecutionContext) extends FrontendBaseController with Logging {

  def logout: Action[AnyContent] = identify { request =>

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    logger.info(s"[Session ID: ${utils.Session.id(hc)}] user signed out from the service, asking for feedback")

    if(appConfig.logoutAudit) {

      val auditData = Map(
        "sessionId" -> Session.id(hc),
        "event" -> "signout",
        "service" -> "register-trust-other-individual-frontend",
        "userGroup" -> request.affinityGroup.toString
      )

      auditConnector.sendExplicitAudit(
        "trusts",
        auditData
      )

    }

    Redirect(appConfig.logoutUrl).withSession(session = ("feedbackId", Session.id(hc)))

  }
}