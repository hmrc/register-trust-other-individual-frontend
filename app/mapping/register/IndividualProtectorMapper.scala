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

import javax.inject.Inject
import mapping.Mapping
import mapping.reads.{IndividualProtector, IndividualProtectors}
import models.{PassportOrIdCardDetails, UserAnswers}

class IndividualProtectorMapper @Inject()(nameMapper: NameMapper,
                                          addressMapper: AddressMapper) extends Mapping[List[Protector]] {
  override def build(userAnswers: UserAnswers): Option[List[Protector]] = {

    val protectors: List[IndividualProtector] =
      userAnswers.get(IndividualProtectors).getOrElse(List.empty)

    protectors match {
      case Nil => None
      case list =>
        Some(
          list.map { protector =>
            Protector(
              name = nameMapper.build(protector.name),
              dateOfBirth = protector.dateOfBirth,
              identification = buildIdentification(protector)
            )
          }
        )
    }
  }

  private def buildIdentification(protector: IndividualProtector): Option[IdentificationType] = {
    val nino = protector.nationalInsuranceNumber
    val address = (protector.ukAddress, protector.internationalAddress) match {
      case (None, None) => None
      case (Some(address), _) => addressMapper.build(address)
      case (_, Some(address)) => addressMapper.build(address)
    }
    val passport = protector.passportDetails
    val idCard = protector.idCardDetails
    (nino, address, passport, idCard) match {
      case (None, None, None, None) => None
      case (Some(_), _, _, _) => Some(IdentificationType(nino, None, None))
      case (_, _, _, _) =>
        Some(IdentificationType(
          nino = None,
          passport = buildPassportOrIdCard(protector.passportDetails, protector.idCardDetails),
          address = address
        )
        )
    }
  }

  private def buildPassportOrIdCard(passport: Option[PassportOrIdCardDetails], idCardDetails: Option[PassportOrIdCardDetails]) =
    (passport, idCardDetails) match {
      case (Some(passport), _) => buildPassport(passport)
      case (_, Some(idCard)) => buildPassport(idCard)
      case (None, None) => None
    }

  private def buildPassport(details: PassportOrIdCardDetails) =
    Some(PassportType(details.cardNumber, details.expiryDate, details.country))
}
