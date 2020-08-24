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

package config

import com.google.inject.AbstractModule
import config.annotations.{BusinessProtector, IndividualProtector}
import controllers.actions.register._
import navigation.{BusinessProtectorNavigator, IndividualProtectorNavigator, Navigator, ProtectorNavigator}
import repositories.{DefaultRegistrationsRepository, RegistrationsRepository}

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[RegistrationsRepository]).to(classOf[DefaultRegistrationsRepository]).asEagerSingleton()
    bind(classOf[RegistrationDataRequiredAction]).to(classOf[RegistrationDataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[DraftIdRetrievalActionProvider]).to(classOf[DraftIdDataRetrievalActionProviderImpl]).asEagerSingleton()

    bind(classOf[Navigator]).annotatedWith(classOf[BusinessProtector]).to(classOf[BusinessProtectorNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[IndividualProtector]).to(classOf[IndividualProtectorNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).to(classOf[ProtectorNavigator]).asEagerSingleton()
  }
}