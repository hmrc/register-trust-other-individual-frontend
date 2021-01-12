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

package mapping.register

import java.time.LocalDate

import models.FullName
import play.api.libs.json._

/**
  * Trust Registration API Schema - definitions models below
  */

case class OtherIndividualsType(otherIndividual: Option[List[OtherIndividual]],
                          otherIndividualCompany: Option[List[OtherIndividualCompany]])

object OtherIndividualsType {
  implicit val otherIndividualsTypeFormat: Format[OtherIndividualsType] = Json.format[OtherIndividualsType]
}

case class OtherIndividual(name: FullName,
                           dateOfBirth: Option[LocalDate],
                           identification: Option[IdentificationType])

object OtherIndividual {
  implicit val otherIndividualFormat: Format[OtherIndividual] = Json.format[OtherIndividual]
}

case class OtherIndividualCompany(name: String,
                            identification: Option[IdentificationOrgType])

object OtherIndividualCompany {
  implicit val otherIndividualCompanyFormat: Format[OtherIndividualCompany] = Json.format[OtherIndividualCompany]
}

case class IdentificationType(nino: Option[String],
                              passport: Option[PassportType],
                              address: Option[AddressType])

object IdentificationType {
  implicit val identificationTypeFormat: Format[IdentificationType] = Json.format[IdentificationType]
}

case class IdentificationOrgType(utr: Option[String],
                                 address: Option[AddressType])

object IdentificationOrgType {
  implicit val identificationOrgTypeFormat: Format[IdentificationOrgType] = Json.format[IdentificationOrgType]
}

case class PassportType(number: String,
                        expirationDate: LocalDate,
                        countryOfIssue: String)

object PassportType {
  implicit val passportTypeFormat: Format[PassportType] = Json.format[PassportType]
}

case class AddressType(line1: String,
                       line2: String,
                       line3: Option[String],
                       line4: Option[String],
                       postCode: Option[String],
                       country: String)

object AddressType {
  implicit val addressTypeFormat: Format[AddressType] = Json.format[AddressType]
}



