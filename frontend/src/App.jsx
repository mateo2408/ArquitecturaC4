import { useState } from 'react'
import axios from 'axios'
import './index.css'

const API_URL = 'http://localhost:8080/api'

function App() {
  const [step, setStep] = useState(1)
  const [loading, setLoading] = useState(false)

  // Form data
  const [formData, setFormData] = useState({
    correo: '',
    ruc: '',
    placa: '',
    cedula: ''
  })

  // Responses
  const [contribuyenteData, setContribuyenteData] = useState(null)
  const [vehiculoData, setVehiculoData] = useState(null)
  const [licenciaData, setLicenciaData] = useState(null)

  // Errors
  const [errors, setErrors] = useState({})
  const [apiError, setApiError] = useState('')

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
    // Clear error when user types
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
    setApiError('')
  }

  const validateEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
  }

  const validateRUC = (ruc) => {
    return /^\d{13}$/.test(ruc)
  }

  const validateCedula = (cedula) => {
    return /^\d{10}$/.test(cedula)
  }

  const validatePlaca = (placa) => {
    return /^[A-Z]{3}-\d{3,4}$|^[A-Z]{2}-\d{4}$/.test(placa)
  }

  // Step 1: Verificar Contribuyente
  const handleStep1Submit = async (e) => {
    e.preventDefault()
    setErrors({})
    setApiError('')

    // Validate
    const newErrors = {}
    if (!formData.correo) {
      newErrors.correo = 'El correo es obligatorio'
    } else if (!validateEmail(formData.correo)) {
      newErrors.correo = 'El correo no es válido'
    }
    if (!formData.ruc) {
      newErrors.ruc = 'El RUC es obligatorio'
    } else if (!validateRUC(formData.ruc)) {
      newErrors.ruc = 'El RUC debe tener 13 dígitos'
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      return
    }

    setLoading(true)
    try {
      const response = await axios.post(`${API_URL}/verificar-contribuyente`, {
        correo: formData.correo,
        ruc: formData.ruc
      })

      setContribuyenteData(response.data)

      if (!response.data.esContribuyente) {
        setApiError('El RUC no está registrado como contribuyente en el SRI')
      } else if (!response.data.esPersonaNatural) {
        setApiError('El RUC no corresponde a una persona natural. Este sistema solo trabaja con personas naturales.')
      } else {
        setStep(2)
      }
    } catch (error) {
      setApiError(error.response?.data?.message || 'Error al verificar el contribuyente')
      console.error('Error:', error)
    } finally {
      setLoading(false)
    }
  }

  // Step 2: Consultar Vehículo
  const handleStep2Submit = async (e) => {
    e.preventDefault()
    setErrors({})
    setApiError('')

    if (!formData.placa) {
      setErrors({ placa: 'La placa es obligatoria' })
      return
    }
    if (!validatePlaca(formData.placa.toUpperCase())) {
      setErrors({ placa: 'Formato de placa inválido (ej: ABC-1234)' })
      return
    }

    setLoading(true)
    try {
      const response = await axios.post(`${API_URL}/consultar-vehiculo`, {
        placa: formData.placa.toUpperCase()
      })

      setVehiculoData(response.data)

      if (!response.data.encontrado) {
        setApiError('No se encontró información del vehículo')
      } else {
        // Extract cedula from RUC (first 10 digits)
        const cedulaFromRuc = formData.ruc.substring(0, 10)
        setFormData(prev => ({ ...prev, cedula: cedulaFromRuc }))
        setStep(3)
      }
    } catch (error) {
      setApiError(error.response?.data?.message || 'Error al consultar el vehículo')
      console.error('Error:', error)
    } finally {
      setLoading(false)
    }
  }

  // Step 3: Consultar Licencia
  const handleStep3Submit = async (e) => {
    e.preventDefault()
    setErrors({})
    setApiError('')

    if (!formData.cedula) {
      setErrors({ cedula: 'La cédula es obligatoria' })
      return
    }
    if (!validateCedula(formData.cedula)) {
      setErrors({ cedula: 'La cédula debe tener 10 dígitos' })
      return
    }

    setLoading(true)
    try {
      const response = await axios.post(`${API_URL}/consultar-licencia`, {
        cedula: formData.cedula,
        placa: formData.placa.toUpperCase()
      })

      setLicenciaData(response.data)
      setStep(4)
    } catch (error) {
      setApiError(error.response?.data?.message || 'Error al consultar la licencia')
      console.error('Error:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleReset = () => {
    setStep(1)
    setFormData({ correo: '', ruc: '', placa: '', cedula: '' })
    setContribuyenteData(null)
    setVehiculoData(null)
    setLicenciaData(null)
    setErrors({})
    setApiError('')
  }

  return (
    <div className="container">
      <h1>🇪🇨 Consultas SRI y ANT - Ecuador</h1>

      {/* Progress Bar */}
      <div className="progress-bar">
        <div className={`progress-step ${step >= 1 ? 'active' : ''} ${step > 1 ? 'completed' : ''}`}>
          <div className="progress-circle">1</div>
          <span className="progress-label">Contribuyente</span>
        </div>
        <div className={`progress-step ${step >= 2 ? 'active' : ''} ${step > 2 ? 'completed' : ''}`}>
          <div className="progress-circle">2</div>
          <span className="progress-label">Vehículo</span>
        </div>
        <div className={`progress-step ${step >= 3 ? 'active' : ''} ${step > 3 ? 'completed' : ''}`}>
          <div className="progress-circle">3</div>
          <span className="progress-label">Licencia</span>
        </div>
        <div className={`progress-step ${step >= 4 ? 'active' : ''}`}>
          <div className="progress-circle">✓</div>
          <span className="progress-label">Resumen</span>
        </div>
      </div>

      {/* Step 1: Verificar Contribuyente */}
      {step === 1 && (
        <div className="step-container">
          <div className="step-title">
            <span className="step-number">1</span>
            <h2>Verificar Contribuyente SRI</h2>
          </div>

          <form onSubmit={handleStep1Submit}>
            <div className="form-group">
              <label htmlFor="correo">Correo Electrónico *</label>
              <input
                type="email"
                id="correo"
                name="correo"
                value={formData.correo}
                onChange={handleInputChange}
                className={errors.correo ? 'error' : ''}
                placeholder="ejemplo@correo.com"
              />
              {errors.correo && <div className="error-message">{errors.correo}</div>}
            </div>

            <div className="form-group">
              <label htmlFor="ruc">RUC (Persona Natural) *</label>
              <input
                type="text"
                id="ruc"
                name="ruc"
                value={formData.ruc}
                onChange={handleInputChange}
                className={errors.ruc ? 'error' : ''}
                placeholder="1234567890001"
                maxLength="13"
              />
              {errors.ruc && <div className="error-message">{errors.ruc}</div>}
              <small style={{color: '#666', display: 'block', marginTop: '5px'}}>
                El RUC debe tener 13 dígitos y debe ser de una persona natural
              </small>
            </div>

            {apiError && (
              <div className="alert alert-error">
                {apiError}
              </div>
            )}

            {contribuyenteData && !contribuyenteData.esContribuyente && (
              <div className="result-card error">
                <div className="result-title">⚠️ No se pudo verificar</div>
                <p>{contribuyenteData.mensaje}</p>
              </div>
            )}

            {loading ? (
              <div className="loading">
                <div className="spinner"></div>
                <p>Verificando contribuyente...</p>
              </div>
            ) : (
              <button type="submit" className="button">
                Verificar Contribuyente
              </button>
            )}
          </form>
        </div>
      )}

      {/* Step 2: Consultar Vehículo */}
      {step === 2 && (
        <div className="step-container">
          <div className="step-title">
            <span className="step-number">2</span>
            <h2>Consultar Vehículo</h2>
          </div>

          {contribuyenteData && (
            <div className="result-card success">
              <div className="result-title">✓ Contribuyente Verificado</div>
              <div className="result-item">
                <span className="result-label">RUC:</span>
                <span className="result-value">{contribuyenteData.datosContribuyente.numeroRuc}</span>
              </div>
              <div className="result-item">
                <span className="result-label">Nombre:</span>
                <span className="result-value">{contribuyenteData.datosContribuyente.razonSocial}</span>
              </div>
              <div className="result-item">
                <span className="result-label">Estado:</span>
                <span className="result-value">{contribuyenteData.datosContribuyente.estadoContribuyente}</span>
              </div>
            </div>
          )}

          <form onSubmit={handleStep2Submit}>
            <div className="form-group">
              <label htmlFor="placa">Placa del Vehículo *</label>
              <input
                type="text"
                id="placa"
                name="placa"
                value={formData.placa}
                onChange={handleInputChange}
                className={errors.placa ? 'error' : ''}
                placeholder="ABC-1234"
                maxLength="8"
                style={{textTransform: 'uppercase'}}
              />
              {errors.placa && <div className="error-message">{errors.placa}</div>}
              <small style={{color: '#666', display: 'block', marginTop: '5px'}}>
                Formato: ABC-1234 o AB-1234
              </small>
            </div>

            {apiError && (
              <div className="alert alert-error">
                {apiError}
              </div>
            )}

            {loading ? (
              <div className="loading">
                <div className="spinner"></div>
                <p>Consultando vehículo...</p>
              </div>
            ) : (
              <div>
                <button type="submit" className="button">
                  Consultar Vehículo
                </button>
                <button type="button" className="button button-secondary" onClick={() => setStep(1)}>
                  Regresar
                </button>
              </div>
            )}
          </form>
        </div>
      )}

      {/* Step 3: Consultar Licencia */}
      {step === 3 && (
        <div className="step-container">
          <div className="step-title">
            <span className="step-number">3</span>
            <h2>Consultar Puntos de Licencia</h2>
          </div>

          {vehiculoData && (
            <div className="result-card success">
              <div className="result-title">✓ Vehículo Encontrado</div>
              <div className="result-item">
                <span className="result-label">Placa:</span>
                <span className="result-value">{vehiculoData.datosVehiculo.placa}</span>
              </div>
              <div className="result-item">
                <span className="result-label">Marca:</span>
                <span className="result-value">{vehiculoData.datosVehiculo.marca}</span>
              </div>
              <div className="result-item">
                <span className="result-label">Modelo:</span>
                <span className="result-value">{vehiculoData.datosVehiculo.modelo}</span>
              </div>
              <div className="result-item">
                <span className="result-label">Año:</span>
                <span className="result-value">{vehiculoData.datosVehiculo.anio}</span>
              </div>
              <div className="result-item">
                <span className="result-label">Color:</span>
                <span className="result-value">{vehiculoData.datosVehiculo.color}</span>
              </div>
            </div>
          )}

          <div className="alert alert-info">
            ℹ️ La consulta a la ANT puede tomar unos segundos. El servicio tiene baja disponibilidad y se realizarán hasta 3 intentos automáticos.
          </div>

          <form onSubmit={handleStep3Submit}>
            <div className="form-group">
              <label htmlFor="cedula">Cédula del Conductor *</label>
              <input
                type="text"
                id="cedula"
                name="cedula"
                value={formData.cedula}
                onChange={handleInputChange}
                className={errors.cedula ? 'error' : ''}
                placeholder="1234567890"
                maxLength="10"
              />
              {errors.cedula && <div className="error-message">{errors.cedula}</div>}
              <small style={{color: '#666', display: 'block', marginTop: '5px'}}>
                Cédula de 10 dígitos (se completó automáticamente desde el RUC)
              </small>
            </div>

            {apiError && (
              <div className="alert alert-error">
                {apiError}
              </div>
            )}

            {loading ? (
              <div className="loading">
                <div className="spinner"></div>
                <p>Consultando puntos de licencia en la ANT...</p>
                <p style={{fontSize: '14px', marginTop: '10px'}}>
                  Esto puede tomar hasta 30 segundos debido a la disponibilidad del servicio
                </p>
              </div>
            ) : (
              <div>
                <button type="submit" className="button">
                  Consultar Licencia
                </button>
                <button type="button" className="button button-secondary" onClick={() => setStep(2)}>
                  Regresar
                </button>
              </div>
            )}
          </form>
        </div>
      )}

      {/* Step 4: Resumen Final */}
      {step === 4 && (
        <div className="step-container">
          <div className="step-title">
            <span className="step-number">✓</span>
            <h2>Resumen Completo</h2>
          </div>

          <div className="alert alert-success">
            ✓ Consulta completada exitosamente
          </div>

          {/* Contribuyente */}
          <div className="result-card">
            <div className="result-title">📋 Datos del Contribuyente</div>
            <div className="result-item">
              <span className="result-label">RUC:</span>
              <span className="result-value">{contribuyenteData?.datosContribuyente.numeroRuc}</span>
            </div>
            <div className="result-item">
              <span className="result-label">Nombre:</span>
              <span className="result-value">{contribuyenteData?.datosContribuyente.razonSocial}</span>
            </div>
            <div className="result-item">
              <span className="result-label">Tipo:</span>
              <span className="result-value">{contribuyenteData?.datosContribuyente.tipoContribuyente}</span>
            </div>
            <div className="result-item">
              <span className="result-label">Estado:</span>
              <span className="result-value">{contribuyenteData?.datosContribuyente.estadoContribuyente}</span>
            </div>
          </div>

          {/* Vehículo */}
          <div className="result-card">
            <div className="result-title">🚗 Datos del Vehículo</div>
            <div className="result-item">
              <span className="result-label">Placa:</span>
              <span className="result-value">{vehiculoData?.datosVehiculo.placa}</span>
            </div>
            <div className="result-item">
              <span className="result-label">Marca:</span>
              <span className="result-value">{vehiculoData?.datosVehiculo.marca}</span>
            </div>
            <div className="result-item">
              <span className="result-label">Modelo:</span>
              <span className="result-value">{vehiculoData?.datosVehiculo.modelo}</span>
            </div>
            <div className="result-item">
              <span className="result-label">Año:</span>
              <span className="result-value">{vehiculoData?.datosVehiculo.anio}</span>
            </div>
            <div className="result-item">
              <span className="result-label">Color:</span>
              <span className="result-value">{vehiculoData?.datosVehiculo.color}</span>
            </div>
            <div className="result-item">
              <span className="result-label">Clase:</span>
              <span className="result-value">{vehiculoData?.datosVehiculo.clase}</span>
            </div>
          </div>

          {/* Licencia */}
          {licenciaData && (
            <div className={`result-card ${licenciaData.consultaExitosa ? 'success' : 'warning'}`}>
              <div className="result-title">
                {licenciaData.consultaExitosa ? '🪪 Puntos de Licencia' : '⚠️ Consulta de Licencia'}
              </div>

              {licenciaData.consultaExitosa ? (
                <>
                  <div className="result-item">
                    <span className="result-label">Puntos Disponibles:</span>
                    <span className="result-value" style={{
                      fontSize: '1.5rem',
                      fontWeight: 'bold',
                      color: licenciaData.puntosLicencia >= 20 ? '#28a745' : licenciaData.puntosLicencia >= 10 ? '#ffc107' : '#dc3545'
                    }}>
                      {licenciaData.puntosLicencia} / 30
                    </span>
                  </div>
                  <div className="result-item">
                    <span className="result-label">Citaciones:</span>
                    <span className="result-value">{licenciaData.citaciones}</span>
                  </div>
                  {licenciaData.enCache && (
                    <div className="result-item">
                      <small style={{color: '#666'}}>ℹ️ Datos recuperados del caché</small>
                    </div>
                  )}
                </>
              ) : (
                <div>
                  <p>{licenciaData.mensaje}</p>
                  <small style={{color: '#666', display: 'block', marginTop: '10px'}}>
                    Los datos pueden estar disponibles en caché si se consultaron anteriormente.
                  </small>
                </div>
              )}
            </div>
          )}

          <div style={{marginTop: '30px', textAlign: 'center'}}>
            <button className="button" onClick={handleReset}>
              Nueva Consulta
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

export default App

